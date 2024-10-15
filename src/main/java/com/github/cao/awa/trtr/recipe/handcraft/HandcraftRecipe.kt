package com.github.cao.awa.trtr.recipe.handcraft

import com.github.cao.awa.catheter.Catheter
import com.github.cao.awa.trtr.codec.TrtrCodec
import com.github.cao.awa.trtr.codec.TrtrPacketCodec
import com.github.cao.awa.trtr.mixin.recipe.RecipeManagerAccessor
import com.github.cao.awa.trtr.pair.IntegerRange
import com.github.cao.awa.trtr.pair.item.ItemStackPair
import com.github.cao.awa.trtr.recipe.TrtrRecipeSerializer
import com.github.cao.awa.trtr.recipe.TrtrRecipeType
import com.github.cao.awa.trtr.recipe.handcraft.result.HandcraftRecipeResult
import com.github.cao.awa.trtr.recipe.match.PatternTestableRecipe
import com.github.cao.awa.trtr.recipe.player.HandcraftingPlayer
import com.google.errorprone.annotations.DoNotCall
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.network.RegistryByteBuf
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.RecipeType
import net.minecraft.registry.RegistryWrapper.WrapperLookup
import net.minecraft.world.World
import java.util.*
import java.util.function.BiFunction
import java.util.function.Consumer

abstract class HandcraftRecipe(val input: HandcraftRecipeMakings, val results: MutableList<HandcraftRecipeResult>) :
    Recipe<HandcraftRecipeInput>, PatternTestableRecipe {
    private var resultMaximumTime: Int = 0

    init {
        this.resultMaximumTime = Catheter.of(this.results)
            .varyTo(HandcraftRecipeResult::validateRange)
            .varyTo(IntegerRange::max)
            .sort(Integer::compare)
            .optionalFirst()
            .get()
    }

    override fun matches(input: HandcraftRecipeInput, world: World): Boolean {
        return false
    }

    @DoNotCall
    final override fun craft(input: HandcraftRecipeInput, lookup: WrapperLookup): ItemStack? = null

    abstract fun crafts(input: HandcraftRecipeInput, lookup: WrapperLookup): List<ItemStack>

    override fun fits(width: Int, height: Int): Boolean {
        return false
    }

    @DoNotCall
    override fun getResult(registriesLookup: WrapperLookup): ItemStack? {
        return null
    }

    override fun getSerializer(): RecipeSerializer<*> = TrtrRecipeSerializer.HANDCRAFT_FORGING

    override fun getType(): RecipeType<*> = TrtrRecipeType.HANDCRAFT_FORGING

    fun whenInRange(user: PlayerEntity, action: Consumer<List<HandcraftRecipeResult>>) {
        user as HandcraftingPlayer

        action.accept(
            this.results.filter {
                it.validateRange().isIn(user.handcraftInput().usageTicks)
            }.toList()
        )
    }

    fun tickCraft(world: World, user: PlayerEntity, resultTo: Consumer<List<HandcraftRecipeResult>>) {
        user as HandcraftingPlayer
        if (user.handcraftInput() == null) {
            val input = HandcraftRecipeInput(
                ItemStackPair(
                    user.mainHandStack,
                    user.offHandStack
                ),
                0
            )
            user.handcraftInput(input)

            user.handcraftRecipe(this)
        } else {
            user.handcraftInput().tickUsage()
        }

        if (user.handcraftInput().usageTicks == this.resultMaximumTime) {
            finishingCraft(world, user, resultTo)
        } else if (user.isUsingItem) {
            if (checkInput(world, user)) {
                tickCrafting(world, user, resultTo)
            } else {
                finishingCraft(world, user, resultTo)
            }
        }
    }

    private fun checkInput(world: World, user: PlayerEntity): Boolean {
        user as HandcraftingPlayer

        var successes = true

        this.input.doConsumes.let { consumes ->
            user.handcraftInput().ingredients.let {
                successes = successes && user.mainHandStack == it.main && user.mainHandStack.count >= consumes.first
                successes = successes && user.offHandStack == it.off && user.offHandStack.count >= consumes.second
            }
        }

        return successes
    }

    abstract fun tickCrafting(world: World, user: PlayerEntity, resultTo: Consumer<List<HandcraftRecipeResult>>)

    fun consumeCraft(user: PlayerEntity) {
        this.input.doConsumes.let {
            if (it.first > 0) {
                user.mainHandStack.decrement(it.first)
            }

            if (it.second > 0) {
                user.offHandStack.decrement(it.second)
            }
        }
    }

    fun consumeAndCraft(world: World, user: PlayerEntity, resultTo: Consumer<List<HandcraftRecipeResult>>) {
        this.input.doConsumes.let {
            if (it.first > 0) {
                user.mainHandStack.decrement(it.first)
            }

            if (it.second > 0) {
                user.offHandStack.decrement(it.second)
            }
        }

        stopCraft(world, user, resultTo)
    }

    abstract fun finishingCraft(world: World, user: PlayerEntity, resultTo: Consumer<List<HandcraftRecipeResult>>)

    companion object {
        @JvmStatic
        fun <T : HandcraftRecipe> decode(
            buf: RegistryByteBuf,
            creator: BiFunction<HandcraftRecipeMakings, List<HandcraftRecipeResult>, T>
        ): T {
            return creator.apply(
                TrtrPacketCodec.HANDCRAFT_RECIPE_MAKINGS.decode(buf),
                TrtrPacketCodec.HANDCRAFT_RECIPE_RESULT_LIST.decode(buf)
            )
        }

        @JvmStatic
        fun encode(buf: RegistryByteBuf, value: HandcraftRecipe) {
            TrtrPacketCodec.HANDCRAFT_RECIPE_MAKINGS.encode(buf, value.input)
            TrtrPacketCodec.HANDCRAFT_RECIPE_RESULT_LIST.encode(buf, value.results)
        }

        @JvmStatic
        fun <T : HandcraftRecipe> forGetterInput(): RecordCodecBuilder<T, HandcraftRecipeMakings> =
            TrtrCodec.HANDCRAFT_RECIPE_INPUT.fieldOf("input").forGetter(HandcraftRecipe::input)

        @JvmStatic
        fun <T : HandcraftRecipe> forGetterResult(): RecordCodecBuilder<T, List<HandcraftRecipeResult>> =
            TrtrCodec.HANDCRAFT_RECIPE_RESULT.listOf().fieldOf("results").forGetter(HandcraftRecipe::results)

        @JvmStatic
        fun canCraft(world: World, user: PlayerEntity): Optional<HandcraftRecipe> {
            user as HandcraftingPlayer
            if (user.handcraftRecipe() != null) {
                return Optional.of(user.handcraftRecipe())
            }

            for (type in TrtrRecipeType.ALL_HANDCRAFTING) {
                for (entry in (world.recipeManager as RecipeManagerAccessor).recipesByType[type]) {
                    val recipe = entry.value as HandcraftRecipe
                    if (recipe.isPatternMatches(world, user)) {
                        return Optional.of(recipe)
                    }
                }
            }

            return Optional.empty()
        }

        @JvmStatic
        fun stopCraft(world: World, user: PlayerEntity, resultTo: Consumer<List<HandcraftRecipeResult>>) {
            user as HandcraftingPlayer

            if (user.handcraftRecipe() != null) {
                user.handcraftRecipe().finishingCraft(world, user, resultTo)
            }

            cleanupCraft(user)
        }

        fun cleanupCraft(user: PlayerEntity) {
            user as HandcraftingPlayer

            user.handcraftInput(null)
            user.handcraftRecipe(null)
        }
    }
}

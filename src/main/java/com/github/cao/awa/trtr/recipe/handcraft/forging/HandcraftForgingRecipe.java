package com.github.cao.awa.trtr.recipe.handcraft.forging;

import com.github.cao.awa.catheter.pair.IntegerPair;
import com.github.cao.awa.trtr.TrtrMod;
import com.github.cao.awa.trtr.pair.ingredient.IngredientPair;
import com.github.cao.awa.trtr.recipe.TrtrRecipeSerializer;
import com.github.cao.awa.trtr.recipe.TrtrRecipeType;
import com.github.cao.awa.trtr.recipe.handcraft.HandcraftRecipe;
import com.github.cao.awa.trtr.recipe.handcraft.HandcraftRecipeInput;
import com.github.cao.awa.trtr.recipe.handcraft.HandcraftRecipeMakings;
import com.github.cao.awa.trtr.recipe.handcraft.result.HandcraftRecipeCompletedResult;
import com.github.cao.awa.trtr.recipe.handcraft.result.HandcraftRecipeResult;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class HandcraftForgingRecipe extends HandcraftRecipe {
    public HandcraftForgingRecipe(HandcraftRecipeMakings input, List<HandcraftRecipeResult> results) {
        super(input, results);
    }

    @Override
    public boolean matches(@NotNull HandcraftRecipeInput input, @NotNull World world) {
        return false;
    }

    @Override
    @NotNull
    public List<ItemStack> crafts(@NotNull HandcraftRecipeInput input, @NotNull RegistryWrapper.WrapperLookup lookup) {
        return null;
    }

    @Override
    public boolean fits(int width, int height) {
        return false;
    }

    @Override
    @NotNull
    public RecipeSerializer<?> getSerializer() {
        return TrtrRecipeSerializer.HANDCRAFT_FORGING;
    }

    @Override
    @NotNull
    public RecipeType<?> getType() {
        return TrtrRecipeType.HANDCRAFT_FORGING;
    }

    @NotNull
    public static HandcraftForgingRecipe decode(@NotNull RegistryByteBuf buf) {
        return HandcraftRecipe.decode(buf, HandcraftForgingRecipe::new);
    }

    public static void encode(@NotNull RegistryByteBuf buf, @NotNull HandcraftForgingRecipe value) {
        HandcraftRecipe.encode(buf, value);
    }

    @Override
    public boolean isPatternMatches(World world, PlayerEntity player) {
        ItemStack mainStack = player.getMainHandStack();
        ItemStack offStack = player.getOffHandStack();

        IngredientPair ingredients = getInput().ingredients();
        IntegerPair doConsume = getInput().doConsumes();

        boolean successes = true;

        if (mainStack != null) {
            successes &= ingredients.main().test(mainStack);
            successes &= doConsume.first() <= mainStack.getCount();
        }

        if (offStack != null) {
            successes &= ingredients.off().test(mainStack);
            successes &= doConsume.second() <= offStack.getCount();
        }

        return successes;
    }

    @Override
    public void tickCrafting(@NotNull World world, @NotNull PlayerEntity user, @NotNull Consumer<List<HandcraftRecipeResult>> resultTo) {

    }

    @Override
    public void finishingCraft(@NotNull World world, @NotNull PlayerEntity user, @NotNull Consumer<List<HandcraftRecipeResult>> resultTo) {
        whenInRange(user, inRanges -> {
            for (HandcraftRecipeResult result : inRanges) {
                ItemStack resultStack = result.result(user);
                TrtrMod.itemInjectManager.injectComponent(resultStack, result.components());
                resultTo.accept(List.of(HandcraftRecipeCompletedResult.of(resultStack)));

                consumeAndCraft(world, user, resultTo);
            }
        });
    }
}

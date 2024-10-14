package com.github.cao.awa.trtr.recipe.handcraft.result

import com.github.cao.awa.apricot.util.collection.ApricotCollectionFactor
import com.github.cao.awa.trtr.datapack.inject.item.component.ItemPropertyInjectComponent
import com.github.cao.awa.trtr.pair.IntegerRange
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack

@JvmRecord
data class HandcraftRecipeCompletedResult(val stack: ItemStack) : HandcraftRecipeResult {
    companion object {
        private val RANGE = IntegerRange.of(-1)

        @JvmStatic
        fun of(stack: ItemStack): HandcraftRecipeCompletedResult {
            return HandcraftRecipeCompletedResult(stack)
        }
    }

    override fun validateRange(): IntegerRange = RANGE

    override fun result(player: PlayerEntity): ItemStack = this.stack.copy()

    override fun components(): MutableList<ItemPropertyInjectComponent<*>> = ApricotCollectionFactor.arrayList()
}

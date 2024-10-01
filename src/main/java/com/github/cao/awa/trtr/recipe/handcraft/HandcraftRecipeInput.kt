package com.github.cao.awa.trtr.recipe.handcraft

import com.github.cao.awa.trtr.pair.item.ItemStackPair
import net.minecraft.item.ItemStack
import net.minecraft.recipe.input.RecipeInput

class HandcraftRecipeInput(val ingredients: ItemStackPair, var usageTicks: Int) : RecipeInput {
    override fun getStackInSlot(slot: Int): ItemStack? = null

    override fun getSize(): Int = 2

    fun tickUsage() = this.usageTicks++
}

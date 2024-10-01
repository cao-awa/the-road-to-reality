package com.github.cao.awa.trtr.pair.item

import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.item.ItemStack
import net.minecraft.network.RegistryByteBuf

@JvmRecord
data class ItemStackPair(val main: ItemStack, val off: ItemStack) {
    companion object {
        @JvmStatic
        fun encode(buf: RegistryByteBuf, ingredients: ItemStackPair) {
            ItemStack.PACKET_CODEC.encode(buf, ingredients.main)
            ItemStack.PACKET_CODEC.encode(buf, ingredients.off)
        }

        @JvmStatic
        fun decode(buf: RegistryByteBuf): ItemStackPair {
            val main = ItemStack.PACKET_CODEC.decode(buf)
            val off = ItemStack.PACKET_CODEC.decode(buf)

            return ItemStackPair(
                main,
                off
            )
        }

        @JvmStatic
        fun forGetterMain(): RecordCodecBuilder<ItemStackPair, ItemStack> =
            ItemStack.CODEC.fieldOf("main").forGetter(ItemStackPair::main)

        @JvmStatic
        fun forGetterOff(): RecordCodecBuilder<ItemStackPair, ItemStack> =
            ItemStack.CODEC.fieldOf("off").forGetter(ItemStackPair::off)
    }
}

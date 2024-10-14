package com.github.cao.awa.trtr.recipe.handcraft.result

import com.github.cao.awa.trtr.codec.TrtrPacketCodec
import com.github.cao.awa.trtr.datapack.inject.item.component.ItemPropertyInjectComponent
import com.github.cao.awa.trtr.pair.IntegerRange
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.network.RegistryByteBuf
import net.minecraft.util.Hand

@JvmRecord
data class HandcraftRecipeVaryResult(
    val hand: Hand,
    val count: Int,
    val range: IntegerRange,
    val property: MutableList<ItemPropertyInjectComponent<*>>
) : HandcraftRecipeResult {
    companion object {
        @JvmStatic
        fun decode(buf: RegistryByteBuf): HandcraftRecipeVaryResult {
            return HandcraftRecipeVaryResult(
                TrtrPacketCodec.HAND.decode(buf),
                buf.readInt(),
                TrtrPacketCodec.INT_RANGE.decode(buf),
                TrtrPacketCodec.ITEM_PROPERTY_INJECT_COMPONENT_LIST.decode(buf)
            )
        }

        @JvmStatic
        fun encode(buf: RegistryByteBuf, value: HandcraftRecipeVaryResult) {
            buf.writeByte(HandcraftRecipeResult.VARY_RESULT.toInt())
            TrtrPacketCodec.HAND.encode(buf, value.hand)
            buf.writeInt(value.count)
            TrtrPacketCodec.INT_RANGE.encode(buf, value.range)
            TrtrPacketCodec.ITEM_PROPERTY_INJECT_COMPONENT_LIST.encode(buf, value.property)
        }
    }

    override fun validateRange(): IntegerRange = this.range

    override fun result(player: PlayerEntity): ItemStack {
        player.getStackInHand(this.hand).copy().let {
            if (this.count != -1) {
                it.count = this.count
            }
            return it
        }
    }

    override fun components(): MutableList<ItemPropertyInjectComponent<*>> = this.property
}

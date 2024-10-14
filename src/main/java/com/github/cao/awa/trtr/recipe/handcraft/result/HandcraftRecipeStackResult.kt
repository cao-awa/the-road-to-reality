package com.github.cao.awa.trtr.recipe.handcraft.result

import com.github.cao.awa.trtr.codec.TrtrCodec
import com.github.cao.awa.trtr.codec.TrtrPacketCodec
import com.github.cao.awa.trtr.datapack.inject.item.component.ItemPropertyInjectComponent
import com.github.cao.awa.trtr.pair.IntegerRange
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.network.RegistryByteBuf

@JvmRecord
data class HandcraftRecipeStackResult(
    val stack: ItemStack,
    val range: IntegerRange,
    val property: MutableList<ItemPropertyInjectComponent<*>>
) : HandcraftRecipeResult {
    companion object {
        @JvmStatic
        fun decode(buf: RegistryByteBuf): HandcraftRecipeStackResult {
            return HandcraftRecipeStackResult(
                ItemStack.PACKET_CODEC.decode(buf),
                TrtrPacketCodec.INT_RANGE.decode(buf),
                TrtrPacketCodec.ITEM_PROPERTY_INJECT_COMPONENT_LIST.decode(buf)
            )
        }

        @JvmStatic
        fun encode(buf: RegistryByteBuf, value: HandcraftRecipeStackResult) {
            buf.writeByte(HandcraftRecipeResult.STACK_RESULT.toInt())
            ItemStack.PACKET_CODEC.encode(buf, value.stack)
            TrtrPacketCodec.INT_RANGE.encode(buf, value.range)
            TrtrPacketCodec.ITEM_PROPERTY_INJECT_COMPONENT_LIST.encode(buf, value.property)
        }

        @JvmStatic
        fun forGetterStack(): RecordCodecBuilder<HandcraftRecipeStackResult, ItemStack> =
            ItemStack.CODEC.fieldOf("stack").forGetter(HandcraftRecipeStackResult::stack)

        @JvmStatic
        fun forGetterProperty(): RecordCodecBuilder<HandcraftRecipeStackResult, MutableList<ItemPropertyInjectComponent<*>>> =
            TrtrCodec.ITEM_PROPERTY_INJECT_COMPONENT.listOf().fieldOf("property")
                .forGetter(HandcraftRecipeStackResult::property)
    }

    override fun validateRange(): IntegerRange = this.range

    override fun result(player: PlayerEntity): ItemStack = this.stack.copy()

    override fun components(): MutableList<ItemPropertyInjectComponent<*>> = this.property
}

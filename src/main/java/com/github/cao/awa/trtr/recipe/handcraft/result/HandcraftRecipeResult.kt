package com.github.cao.awa.trtr.recipe.handcraft.result

import com.github.cao.awa.trtr.codec.TrtrPacketCodec
import com.github.cao.awa.trtr.datapack.inject.item.component.ItemPropertyInjectComponent
import io.netty.handler.codec.DecoderException
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.network.RegistryByteBuf

interface HandcraftRecipeResult {
    companion object {
        const val STACK_RESULT: Byte = 0
        const val VARY_RESULT: Byte = 1

        @JvmStatic
        fun decode(buf: RegistryByteBuf): HandcraftRecipeResult {
            val type = buf.readByte()
            if (type == STACK_RESULT) {
                return TrtrPacketCodec.HANDCRAFT_RECIPE_STACK_RESULT.decode(buf)
            } else if (type == VARY_RESULT) {
                return TrtrPacketCodec.HANDCRAFT_RECIPE_VARY_RESULT.decode(buf)
            }
            throw DecoderException("Unsupported result type: $type")
        }

        @JvmStatic
        fun encode(buf: RegistryByteBuf, value: HandcraftRecipeResult) {
            when (value) {
                is HandcraftRecipeStackResult -> TrtrPacketCodec.HANDCRAFT_RECIPE_STACK_RESULT.encode(buf, value)
                is HandcraftRecipeVaryResult -> TrtrPacketCodec.HANDCRAFT_RECIPE_VARY_RESULT.encode(buf, value)
                else -> throw DecoderException("Unsupported result type: ${value.javaClass.simpleName}")
            }
        }
    }

    fun result(player: PlayerEntity): ItemStack

    fun components(): List<ItemPropertyInjectComponent<*>>
}

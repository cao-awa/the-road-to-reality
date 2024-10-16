package com.github.cao.awa.trtr.datapack.inject.item.component

import com.github.cao.awa.trtr.codec.TrtrPacketCodec
import com.github.cao.awa.trtr.datapack.inject.item.action.ItemPropertyInjectAction
import net.minecraft.component.ComponentType
import net.minecraft.network.RegistryByteBuf

@JvmRecord
data class ItemPropertyInjectComponent<T>(
    val type: ComponentType<*>,
    val action: ItemPropertyInjectAction,
    val value: ItemPropertyInjectComponentValue<T>
) {
    companion object {
        @JvmStatic
        fun decode(buf: RegistryByteBuf): ItemPropertyInjectComponent<*> {
            val value = TrtrPacketCodec.ITEM_PROPERTY_INJECT_COMPONENT_VALUE.decode(buf)

            return ItemPropertyInjectComponent(
                value.componentType!!,
                TrtrPacketCodec.ITEM_PROPERTY_INJECT_ACTION.decode(buf),
                value
            )
        }

        @JvmStatic
        fun encode(buf: RegistryByteBuf, value: ItemPropertyInjectComponent<*>) {
            TrtrPacketCodec.ITEM_PROPERTY_INJECT_COMPONENT_VALUE.encode(buf, value.value)
            TrtrPacketCodec.ITEM_PROPERTY_INJECT_ACTION.encode(buf, value.action)
        }

        @JvmStatic
        fun <X> verified(
            type: ComponentType<*>,
            action: ItemPropertyInjectAction,
            value: ItemPropertyInjectComponentValue<*>
        ): ItemPropertyInjectComponent<X> {
            return ItemPropertyInjectComponent(type, action, value.verified(type))
        }
    }
}

package com.github.cao.awa.trtr.datapack.inject.item.component

import com.github.cao.awa.sinuatum.manipulate.Manipulate
import com.github.cao.awa.trtr.components.TrtrComponentType
import net.minecraft.component.ComponentType
import net.minecraft.network.RegistryByteBuf

@JvmRecord
data class ItemPropertyInjectComponentValue<X>(val value: X?, val componentType: TrtrComponentType<X>?) {
    companion object {
        @JvmStatic
        fun decode(buf: RegistryByteBuf): ItemPropertyInjectComponentValue<*> {
            val componentType = ComponentType.PACKET_CODEC.decode(buf)

            return ItemPropertyInjectComponentValue(
                componentType.packetCodec.decode(buf),
                Manipulate.cast(componentType)
            )
        }

        @JvmStatic
        fun encode(buf: RegistryByteBuf, value: ItemPropertyInjectComponentValue<*>) {
            ComponentType.PACKET_CODEC.encode(buf, value.componentType)
            value.componentType!!.packetCodec.encode(buf, Manipulate.cast(value.value))
        }

        @JvmStatic
        fun unverified(value: Any): ItemPropertyInjectComponentValue<*> {
            return ItemPropertyInjectComponentValue(value, null)
        }
    }

    fun <Y> verified(type: ComponentType<*>): ItemPropertyInjectComponentValue<Y> {
        if (type is TrtrComponentType<*>) {
            type.let {
                return ItemPropertyInjectComponentValue(
                    Manipulate.cast(it.valueCreator.castValue(this.value)),
                    Manipulate.cast(type)
                )
            }
        }

        return ItemPropertyInjectComponentValue(null, null)
    }
}

package com.github.cao.awa.trtr.datapack.inject.item.component

import com.github.cao.awa.trtr.components.TrtrComponentTypes
import com.google.gson.JsonObject
import net.minecraft.component.ComponentType

@JvmRecord
data class ItemComponentInjecting<T>(val type: ComponentType<*>, val value: T) {
    companion object {
        @JvmStatic
        fun create(json: JsonObject): ItemComponentInjecting<*> {
            TrtrComponentTypes.get(json["type"].asString).let {
                val value = it.valueCreator.apply(json["value"])
                return ItemComponentInjecting(it, value)
            }
        }
    }
}

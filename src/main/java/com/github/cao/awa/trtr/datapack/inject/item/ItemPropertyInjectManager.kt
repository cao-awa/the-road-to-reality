package com.github.cao.awa.trtr.datapack.inject.item

import com.github.cao.awa.apricot.util.collection.ApricotCollectionFactor
import com.github.cao.awa.sinuatum.manipulate.Manipulate
import com.github.cao.awa.trtr.TrtrMod
import com.github.cao.awa.trtr.codec.TrtrCodec
import com.github.cao.awa.trtr.datapack.inject.item.action.ItemPropertyInjectAction
import com.github.cao.awa.trtr.datapack.inject.item.action.handler.ItemPropertyInjectHandler
import com.github.cao.awa.trtr.datapack.inject.item.component.ItemPropertyInjectComponent
import com.github.cao.awa.trtr.datapack.inject.item.component.ItemPropertyInjectComponentValue
import com.github.cao.awa.trtr.registry.TrtrRegistryKeys
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.serialization.JsonOps
import net.minecraft.component.ComponentType
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import net.minecraft.resource.JsonDataLoader
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import net.minecraft.util.profiler.Profiler
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class ItemPropertyInjectManager(private val registryLookup: RegistryWrapper.WrapperLookup) :
    JsonDataLoader(GSON, RegistryKeys.getPath(TrtrRegistryKeys.ITEM_PROPERTY_INJECT)) {
    companion object {
        private val LOGGER: Logger = LogManager.getLogger("ItemPropertyInjectManager")
        private val GSON: Gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
    }

    private val injects = ApricotCollectionFactor.hashMap<Item, MutableList<ItemPropertyInject<*>>>()

    override fun apply(prepared: Map<Identifier, JsonElement>, manager: ResourceManager, profiler: Profiler) {
        for (value in prepared.values) {
            value as JsonObject

            val itemTarget = value["target"].asString

            LOGGER.info("Injecting trtr property to item '{}'", itemTarget)

            val registryOps = registryLookup.getOps(JsonOps.INSTANCE)

            var injecting: ItemPropertyInject<*>? = null

            TrtrCodec.ITEM_PROPERTY_INJECT.parse(registryOps, value).let {
                it.result().let { result ->
                    if (result.isPresent) {
                        injecting = result.get()
                    } else {
                        LOGGER.info("Failure inject the property to item '{}'", itemTarget)
                    }
                }
            }

            val item = Registries.ITEM[Identifier.of(itemTarget)]

            this.injects.computeIfAbsent(item) { ApricotCollectionFactor.arrayList() }

            injecting?.let {
                this.injects[item]!!.add(it)
            }
        }
    }

    fun injects(item: Item): List<ItemPropertyInject<*>> {
        return this.injects[item] ?: emptyList()
    }

    fun inject(stack: ItemStack) = injectProperty(stack, injects(stack.item))

    fun injectProperty(stack: ItemStack, injects: List<ItemPropertyInject<*>>) {
        for (inject in injects) {
            injectComponent(stack, inject.components)
        }
    }

    fun injectComponent(stack: ItemStack, injects: List<ItemPropertyInjectComponent<*>>) {
        // Inject to current stack.
        for (component in injects) {
            val value = component.value
            val type = component.type

            // Do not append the preset value when the component is present.
            if (component.action == ItemPropertyInjectAction.SET_PRESET) {
                if (!stack.contains(type)) {
                    injectDefault(stack, type, value)
                }

                continue
            }

            // When the component is present, do actions.
            val currentValue = stack.get(type)
            val calculatedValue = ItemPropertyInjectHandler.doHandles(currentValue, value.value, component.action)

            // Use to debug, trace inject details.
            TrtrMod.debug(
                "Injecting: '{}' as '{}' to item '{}' using '{}'('{}' -> '{}')",
                { type },
                { calculatedValue },
                { Registries.ITEM.getId(stack.item) },
                component::action,
                { currentValue },
                value::value,
                LOGGER::info
            )


            // Modifies present value.
            stack.set(type, Manipulate.cast(calculatedValue))
        }
    }

    fun injectDefault(stack: ItemStack, type: ComponentType<*>, value: ItemPropertyInjectComponentValue<*>) {
        // Use to debug, trace inject details.
        TrtrMod.debug(
            "Injecting: '{}' as '{}' to item '{}' using '{}'",
            { type },
            value::value,
            { Registries.ITEM.getId(stack.item) },
            { ItemPropertyInjectAction.SET_PRESET },
            LOGGER::info
        )

        // Append preset value to item.
        stack.set(type, Manipulate.cast(value.value))
    }
}

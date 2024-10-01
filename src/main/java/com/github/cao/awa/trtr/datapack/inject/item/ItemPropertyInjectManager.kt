package com.github.cao.awa.trtr.datapack.inject.item

import com.github.cao.awa.apricot.util.collection.ApricotCollectionFactor
import com.github.cao.awa.trtr.datapack.inject.item.component.ItemComponentInjecting
import com.github.cao.awa.trtr.registry.TrtrRegistryKeys
import com.google.gson.*
import net.minecraft.item.Item
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

    private val injects = ApricotCollectionFactor.hashMap<Item, MutableList<ItemInjecting>>()

    override fun apply(prepared: Map<Identifier, JsonElement>, manager: ResourceManager, profiler: Profiler) {
        for (value in prepared.values) {
            value as JsonObject

            val injecting = ItemInjecting()

            val itemTarget = value["target"].asString

            LOGGER.info("Injecting trtr property to item '{}'", itemTarget)

            val components = value["components"] as JsonArray

            for (component in components) {
                injecting.addComponent(
                    ItemComponentInjecting.create(component as JsonObject)
                )
            }

            val item = Registries.ITEM[Identifier.of(itemTarget)]

            this.injects.computeIfAbsent(item) { ApricotCollectionFactor.arrayList() }

            this.injects[item]!!.add(injecting)
        }
    }

    fun injects(item: Item): List<ItemInjecting> {
        return this.injects[item] ?: emptyList()
    }
}

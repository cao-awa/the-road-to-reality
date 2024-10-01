package com.github.cao.awa.trtr.datapack.inject.item

import com.github.cao.awa.apricot.util.collection.ApricotCollectionFactor
import com.github.cao.awa.trtr.datapack.inject.item.component.ItemComponentInjecting

class ItemInjecting {
    val components: ArrayList<ItemComponentInjecting<*>> = ApricotCollectionFactor.arrayList()

    fun addComponent(component: ItemComponentInjecting<*>) {
        this.components.add(component)
    }
}

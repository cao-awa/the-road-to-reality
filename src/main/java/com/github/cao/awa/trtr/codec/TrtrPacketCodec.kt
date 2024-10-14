package com.github.cao.awa.trtr.codec

import com.github.cao.awa.apricot.util.collection.ApricotCollectionFactor
import com.github.cao.awa.catheter.pair.IntegerPair
import com.github.cao.awa.trtr.datapack.inject.item.action.ItemPropertyInjectAction
import com.github.cao.awa.trtr.datapack.inject.item.component.ItemPropertyInjectComponent
import com.github.cao.awa.trtr.datapack.inject.item.component.ItemPropertyInjectComponentValue
import com.github.cao.awa.trtr.pair.IntegerRange
import com.github.cao.awa.trtr.pair.ingredient.IngredientPair
import com.github.cao.awa.trtr.recipe.handcraft.HandcraftRecipeMakings
import com.github.cao.awa.trtr.recipe.handcraft.forging.HandcraftForgingRecipe
import com.github.cao.awa.trtr.recipe.handcraft.result.HandcraftRecipeResult
import com.github.cao.awa.trtr.recipe.handcraft.result.HandcraftRecipeStackResult
import com.github.cao.awa.trtr.recipe.handcraft.result.HandcraftRecipeVaryResult
import io.netty.handler.codec.DecoderException
import io.netty.handler.codec.EncoderException
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.util.Hand

object TrtrPacketCodec {
    @JvmField
    val HAND: PacketCodec<RegistryByteBuf, Hand> = PacketCodec.ofStatic({ buf: RegistryByteBuf, hand: Hand? ->
        when (hand) {
            Hand.MAIN_HAND -> buf.writeBoolean(true)
            Hand.OFF_HAND -> buf.writeBoolean(false)
            else -> throw EncoderException("Can only encode main hand and off hand")
        }
    }, { buf: RegistryByteBuf -> if (buf.readBoolean()) Hand.MAIN_HAND else Hand.OFF_HAND })

    @JvmField
    val INT_RANGE: PacketCodec<RegistryByteBuf, IntegerRange> =
        PacketCodec.ofStatic(IntegerRange::encode, IntegerRange::decode)

    @JvmField
    val INGREDIENT_PAIR: PacketCodec<RegistryByteBuf, IngredientPair> =
        PacketCodec.ofStatic(IngredientPair::encode, IngredientPair::decode)

    @JvmField
    val INGREDIENT_CONSUME_PAIR: PacketCodec<RegistryByteBuf, IntegerPair> =
        PacketCodec.ofStatic(IngredientPair::encodeConsume, IngredientPair::decodeConsume)

    @JvmField
    val HANDCRAFT_FORGING_RECIPE: PacketCodec<RegistryByteBuf, HandcraftForgingRecipe> = PacketCodec.ofStatic(
        { buf: RegistryByteBuf, value: HandcraftForgingRecipe ->
            HandcraftForgingRecipe.encode(
                buf,
                value
            )
        },
        { buf: RegistryByteBuf ->
            HandcraftForgingRecipe.decode(
                buf
            )
        })

    @JvmField
    val ITEM_PROPERTY_INJECT_COMPONENT: PacketCodec<RegistryByteBuf, ItemPropertyInjectComponent<*>> =
        PacketCodec.ofStatic(
            { buf: RegistryByteBuf, component: ItemPropertyInjectComponent<*> ->
                ItemPropertyInjectComponent.encode(
                    buf,
                    component
                )
            },
            { buf: RegistryByteBuf -> ItemPropertyInjectComponent.decode(buf) })

    @JvmField
    val ITEM_PROPERTY_INJECT_ACTION: PacketCodec<RegistryByteBuf, ItemPropertyInjectAction> = PacketCodec.ofStatic(
        { buf: RegistryByteBuf, action: ItemPropertyInjectAction ->
            buf.writeByte(action.ordinal)
        },
        { buf: RegistryByteBuf ->
            val act = buf.readByte().toInt()
            if (act >= ItemPropertyInjectAction.entries.size) {
                throw DecoderException("Unsupported action: '$act'")
            }
            ItemPropertyInjectAction.entries[act]
        })

    @JvmField
    val ITEM_PROPERTY_INJECT_COMPONENT_LIST: PacketCodec<RegistryByteBuf, MutableList<ItemPropertyInjectComponent<*>>> =
        ITEM_PROPERTY_INJECT_COMPONENT.collect(PacketCodecs.toCollection(ApricotCollectionFactor::arrayList))

    @JvmField
    val ITEM_PROPERTY_INJECT_COMPONENT_VALUE: PacketCodec<RegistryByteBuf, ItemPropertyInjectComponentValue<*>> =
        PacketCodec.ofStatic(
            { buf: RegistryByteBuf, value: ItemPropertyInjectComponentValue<*> ->
                ItemPropertyInjectComponentValue.encode(
                    buf,
                    value
                )
            },
            { buf: RegistryByteBuf -> ItemPropertyInjectComponentValue.decode(buf) })

    @JvmField
    val HANDCRAFT_RECIPE_RESULT: PacketCodec<RegistryByteBuf, HandcraftRecipeResult> =
        PacketCodec.ofStatic(HandcraftRecipeResult::encode, HandcraftRecipeResult::decode)

    @JvmField
    val HANDCRAFT_RECIPE_RESULT_LIST: PacketCodec<RegistryByteBuf, MutableList<HandcraftRecipeResult>> =
        HANDCRAFT_RECIPE_RESULT.collect(PacketCodecs.toCollection(ApricotCollectionFactor::arrayList))

    @JvmField
    val HANDCRAFT_RECIPE_STACK_RESULT: PacketCodec<RegistryByteBuf, HandcraftRecipeStackResult> =
        PacketCodec.ofStatic(HandcraftRecipeStackResult::encode, HandcraftRecipeStackResult::decode)

    @JvmField
    val HANDCRAFT_RECIPE_VARY_RESULT: PacketCodec<RegistryByteBuf, HandcraftRecipeVaryResult> =
        PacketCodec.ofStatic(HandcraftRecipeVaryResult::encode, HandcraftRecipeVaryResult::decode)

    @JvmField
    val HANDCRAFT_RECIPE_MAKINGS: PacketCodec<RegistryByteBuf, HandcraftRecipeMakings> =
        PacketCodec.ofStatic(HandcraftRecipeMakings::encode, HandcraftRecipeMakings::decode)
}

package com.github.cao.awa.trtr.recipe.handcraft

import com.github.cao.awa.catheter.pair.IntegerPair
import com.github.cao.awa.trtr.codec.TrtrCodec
import com.github.cao.awa.trtr.codec.TrtrPacketCodec
import com.github.cao.awa.trtr.pair.ingredient.IngredientPair
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryByteBuf

@JvmRecord
data class HandcraftRecipeMakings(val ingredients: IngredientPair, val doConsumes: IntegerPair) {
    companion object {
        @JvmStatic
        fun decode(buf: RegistryByteBuf): HandcraftRecipeMakings {
            return HandcraftRecipeMakings(
                TrtrPacketCodec.INGREDIENT_PAIR.decode(buf),
                TrtrPacketCodec.INGREDIENT_CONSUME_PAIR.decode(buf)
            )
        }

        @JvmStatic
        fun encode(buf: RegistryByteBuf, value: HandcraftRecipeMakings) {
            TrtrPacketCodec.INGREDIENT_PAIR.encode(buf, value.ingredients)
            TrtrPacketCodec.INGREDIENT_CONSUME_PAIR.encode(buf, value.doConsumes)
        }

        @JvmStatic
        fun forGetterIngredients(): RecordCodecBuilder<HandcraftRecipeMakings, IngredientPair> =
            TrtrCodec.INGREDIENT_PAIR.fieldOf("ingredients").forGetter(HandcraftRecipeMakings::ingredients)

        @JvmStatic
        fun forGetterDoConsumes(): RecordCodecBuilder<HandcraftRecipeMakings, IntegerPair> =
            TrtrCodec.INGREDIENT_CONSUME_PAIR.fieldOf("do_consumes").forGetter(HandcraftRecipeMakings::doConsumes)
    }
}

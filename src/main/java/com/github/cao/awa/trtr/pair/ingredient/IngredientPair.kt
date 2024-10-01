package com.github.cao.awa.trtr.pair.ingredient

import com.github.cao.awa.catheter.pair.IntegerPair
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryByteBuf
import net.minecraft.recipe.Ingredient

@JvmRecord
data class IngredientPair(val main: Ingredient, val off: Ingredient) {
    companion object {
        @JvmStatic
        fun encode(buf: RegistryByteBuf, ingredients: IngredientPair) {
            Ingredient.PACKET_CODEC.encode(buf, ingredients.main)
            Ingredient.PACKET_CODEC.encode(buf, ingredients.off)
        }

        @JvmStatic
        fun decode(buf: RegistryByteBuf): IngredientPair {
            val main = Ingredient.PACKET_CODEC.decode(buf)
            val off = Ingredient.PACKET_CODEC.decode(buf)

            return IngredientPair(
                main,
                off
            )
        }

        @JvmStatic
        fun encodeConsume(buf: RegistryByteBuf, ingredients: IntegerPair) {
            buf.writeInt(ingredients.first)
            buf.writeInt(ingredients.second)
        }

        @JvmStatic
        fun decodeConsume(buf: RegistryByteBuf): IntegerPair {
            val main = buf.readInt()
            val off = buf.readInt()

            return IntegerPair(
                main,
                off
            )
        }

        @JvmStatic
        fun forGetterMain(): RecordCodecBuilder<IngredientPair, Ingredient> =
            Ingredient.ALLOW_EMPTY_CODEC.fieldOf("main").forGetter(IngredientPair::main)

        @JvmStatic
        fun forGetterOff(): RecordCodecBuilder<IngredientPair, Ingredient> =
            Ingredient.ALLOW_EMPTY_CODEC.fieldOf("off").forGetter(IngredientPair::off)
    }
}

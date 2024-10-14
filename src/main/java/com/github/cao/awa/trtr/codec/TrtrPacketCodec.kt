package com.github.cao.awa.trtr.codec;

import com.github.cao.awa.catheter.pair.IntegerPair;
import com.github.cao.awa.trtr.pair.IntegerRange;
import com.github.cao.awa.trtr.pair.ingredient.IngredientPair;
import com.github.cao.awa.trtr.recipe.handcraft.HandcraftRecipeMakings;
import com.github.cao.awa.trtr.recipe.handcraft.forging.HandcraftForgingRecipe;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public class TrtrPacketCodec {
    public static final PacketCodec<RegistryByteBuf, IntegerRange> INT_RANGE = PacketCodec.ofStatic(IntegerRange::encode, IntegerRange::decode);
    public static final PacketCodec<RegistryByteBuf, IngredientPair> INGREDIENT_PAIR = PacketCodec.ofStatic(IngredientPair::encode, IngredientPair::decode);
    public static final PacketCodec<RegistryByteBuf, IntegerPair> INGREDIENT_CONSUME_PAIR = PacketCodec.ofStatic(IngredientPair::encodeConsume, IngredientPair::decodeConsume);
    public static final PacketCodec<RegistryByteBuf, HandcraftForgingRecipe> HANDCRAFT_FORGING_RECIPE = PacketCodec.ofStatic(HandcraftForgingRecipe::encode, HandcraftForgingRecipe::decode);
    public static final PacketCodec<RegistryByteBuf, HandcraftRecipeMakings> HANDCRAFT_RECIPE_MAKINGS = PacketCodec.ofStatic(HandcraftRecipeMakings::encode, HandcraftRecipeMakings::decode);
}

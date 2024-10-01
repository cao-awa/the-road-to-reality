package com.github.cao.awa.trtr.codec;

import com.github.cao.awa.catheter.pair.IntegerPair;
import com.github.cao.awa.trtr.pair.IntegerRange;
import com.github.cao.awa.trtr.pair.ingredient.IngredientPair;
import com.github.cao.awa.trtr.pair.item.ItemStackPair;
import com.github.cao.awa.trtr.recipe.handcraft.HandcraftRecipe;
import com.github.cao.awa.trtr.recipe.handcraft.HandcraftRecipeMakings;
import com.github.cao.awa.trtr.recipe.handcraft.forging.HandcraftForgingRecipe;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class TrtrCodec {
    public static final MapCodec<IntegerRange> INT_RANGE = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            IntegerRange.forGetterMin(),
            IntegerRange.forGetterMax()
    ).apply(instance, IntegerRange::new));

    public static final MapCodec<IntegerPair> INGREDIENT_CONSUME_PAIR = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Codec.INT.fieldOf("main").orElse(0).forGetter(IntegerPair::first),
            Codec.INT.fieldOf("off").orElse(0).forGetter(IntegerPair::second)
    ).apply(instance, IntegerPair::new));

    public static final MapCodec<IngredientPair> INGREDIENT_PAIR = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            IngredientPair.forGetterMain(),
            IngredientPair.forGetterOff()
    ).apply(instance, IngredientPair::new));

    public static final MapCodec<ItemStackPair> ITEM_STACK_PAIR = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            ItemStackPair.forGetterMain(),
            ItemStackPair.forGetterOff()
    ).apply(instance, ItemStackPair::new));

    public static final MapCodec<HandcraftRecipeMakings> HANDCRAFT_RECIPE_INPUT = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            HandcraftRecipeMakings.forGetterIngredients(),
            HandcraftRecipeMakings.forGetterDoConsumes()
    ).apply(instance, HandcraftRecipeMakings::new));

    public static final MapCodec<HandcraftForgingRecipe> HANDCRAFT_FORGING_RECIPE = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            HandcraftRecipe.forGetterInput(),
            HandcraftRecipe.forGetterResult()
    ).apply(instance, HandcraftForgingRecipe::new));
}

package com.github.cao.awa.trtr.codec;

import com.github.cao.awa.catheter.pair.IntegerPair;
import com.github.cao.awa.trtr.datapack.inject.item.ItemPropertyInject;
import com.github.cao.awa.trtr.datapack.inject.item.action.ItemPropertyInjectAction;
import com.github.cao.awa.trtr.datapack.inject.item.component.ItemPropertyInjectComponent;
import com.github.cao.awa.trtr.datapack.inject.item.component.ItemPropertyInjectComponentValue;
import com.github.cao.awa.trtr.pair.IntegerRange;
import com.github.cao.awa.trtr.pair.ingredient.IngredientPair;
import com.github.cao.awa.trtr.pair.item.ItemStackPair;
import com.github.cao.awa.trtr.recipe.handcraft.HandcraftRecipe;
import com.github.cao.awa.trtr.recipe.handcraft.HandcraftRecipeMakings;
import com.github.cao.awa.trtr.recipe.handcraft.forging.HandcraftForgingRecipe;
import com.github.cao.awa.trtr.recipe.handcraft.result.HandcraftRecipeResult;
import com.github.cao.awa.trtr.recipe.handcraft.result.HandcraftRecipeStackResult;
import com.github.cao.awa.trtr.recipe.handcraft.result.HandcraftRecipeVaryResult;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentType;
import net.minecraft.util.Hand;

import java.util.List;

public class TrtrCodec {
    public static final MapCodec<IntegerRange> INT_RANGE = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            IntegerRange.forGetterMin(),
            IntegerRange.forGetterMax()
    ).apply(instance, IntegerRange::new));

    public static final Codec<Hand> HAND = new PrimitiveCodec<>() {
        @Override
        public <T> DataResult<Hand> read(DynamicOps<T> ops, T input) {
            String hand = input instanceof JsonElement jsonElement ? jsonElement.getAsString() : input.toString();

            return switch (hand) {
                case "off" -> DataResult.success(Hand.OFF_HAND);
                case "main" -> DataResult.success(Hand.MAIN_HAND);
                default -> DataResult.error(() -> "No hand present: '" + hand + "'");
            };
        }

        @Override
        public <T> T write(DynamicOps<T> ops, Hand value) {
            return switch (value) {
                case OFF_HAND -> ops.createString("off");
                case MAIN_HAND -> ops.createString("main");
            };
        }
    };

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

    public static final Codec<ItemPropertyInjectAction> ITEM_PROPERTY_INJECT_ACTION = new PrimitiveCodec<>() {
        @Override
        public <T> DataResult<ItemPropertyInjectAction> read(DynamicOps<T> ops, T input) {
            String action = input instanceof JsonElement jsonElement ? jsonElement.getAsString() : input.toString();

            return switch (action) {
                case "set" -> DataResult.success(ItemPropertyInjectAction.SET);
                case "set_preset" -> DataResult.success(ItemPropertyInjectAction.SET_PRESET);
                case "minus" -> DataResult.success(ItemPropertyInjectAction.MINUS);
                case "add" -> DataResult.success(ItemPropertyInjectAction.ADD);
                case "divide" -> DataResult.success(ItemPropertyInjectAction.DIVIDE);
                case "multiply" -> DataResult.success(ItemPropertyInjectAction.MULTIPLY);
                default -> DataResult.error(() -> "No that action can be completed: '" + action + "'");
            };
        }

        @Override
        public <T> T write(DynamicOps<T> ops, ItemPropertyInjectAction value) {
            return switch (value) {
                case SET -> ops.createString("set");
                case SET_PRESET -> ops.createString("set_preset");
                case MINUS -> ops.createString("minus");
                case ADD -> ops.createString("add");
                case DIVIDE -> ops.createString("divide");
                case MULTIPLY -> ops.createString("multiply");
            };
        }
    };

    public static Codec<ItemPropertyInjectComponent<?>> ITEM_PROPERTY_INJECT_COMPONENT = RecordCodecBuilder.create(instance -> instance.group(
            ComponentType.CODEC.fieldOf("type").forGetter(ItemPropertyInjectComponent::type),
            ITEM_PROPERTY_INJECT_ACTION.fieldOf("action").orElse(ItemPropertyInjectAction.SET_PRESET).forGetter(ItemPropertyInjectComponent::action),
            new PrimitiveCodec<ItemPropertyInjectComponentValue<?>>() {
                @Override
                public <T> DataResult<ItemPropertyInjectComponentValue<?>> read(DynamicOps<T> ops, T input) {
                    return DataResult.success(ItemPropertyInjectComponentValue.unverified(input));
                }

                @Override
                public <T> T write(DynamicOps<T> ops, ItemPropertyInjectComponentValue<?> value) {
                    // Cannot write.
                    return null;
                }
            }.fieldOf("value").forGetter(ItemPropertyInjectComponent::value)
    ).apply(instance, ItemPropertyInjectComponent::verified));

    public static final Codec<ItemPropertyInject<?>> ITEM_PROPERTY_INJECT = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("target").forGetter(ItemPropertyInject::target),
            ITEM_PROPERTY_INJECT_COMPONENT.listOf().fieldOf("components").forGetter(inject -> (List) inject.components())
    ).apply(instance, ItemPropertyInject::generic));

    public static final Codec<HandcraftRecipeStackResult> HANDCRAFT_RECIPE_STACK_RESULT = RecordCodecBuilder.create((instance) -> instance.group(
            HandcraftRecipeStackResult.forGetterStack(),
            HandcraftRecipeStackResult.forGetterProperty()
    ).apply(instance, HandcraftRecipeStackResult::new));

    public static final Codec<HandcraftRecipeVaryResult> HANDCRAFT_RECIPE_VARY_RESULT = RecordCodecBuilder.create((instance) -> instance.group(
            HAND.fieldOf("hand").forGetter(HandcraftRecipeVaryResult::hand),
            Codec.INT.fieldOf("count").orElse(-1).forGetter(HandcraftRecipeVaryResult::count),
            HandcraftRecipeVaryResult.forGetterProperty()
    ).apply(instance, HandcraftRecipeVaryResult::new));

    public static final Codec<HandcraftRecipeResult> HANDCRAFT_RECIPE_RESULT = Codec.xor(
            HANDCRAFT_RECIPE_STACK_RESULT,
            HANDCRAFT_RECIPE_VARY_RESULT
    ).xmap(
            result -> result.map(stack -> stack, vary -> vary),
            result -> {
                if (result instanceof HandcraftRecipeStackResult stack) {
                    return Either.left(stack);
                } else if (result instanceof HandcraftRecipeVaryResult vary) {
                    return Either.right(vary);
                } else {
                    throw new UnsupportedOperationException("This is neither an stack value nor a varying value.");
                }
            }
    );

    public static final MapCodec<HandcraftRecipeMakings> HANDCRAFT_RECIPE_INPUT = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            HandcraftRecipeMakings.forGetterIngredients(),
            HandcraftRecipeMakings.forGetterDoConsumes()
    ).apply(instance, HandcraftRecipeMakings::new));

    public static final MapCodec<HandcraftForgingRecipe> HANDCRAFT_FORGING_RECIPE = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            HandcraftRecipe.forGetterInput(),
            HandcraftRecipe.forGetterResult()
    ).apply(instance, HandcraftForgingRecipe::new));
}

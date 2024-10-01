package com.github.cao.awa.trtr.recipe;

import com.github.cao.awa.trtr.recipe.handcraft.HandcraftRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.List;

public class TrtrRecipeType {
    public static final RecipeType<HandcraftRecipe> HANDCRAFT_FORGING = register("handcraft_forging");

    public static final List<RecipeType<? extends HandcraftRecipe>> ALL_HANDCRAFTING = List.of(HANDCRAFT_FORGING);

    static <T extends Recipe<?>> RecipeType<T> register(final String id) {
        return Registry.register(Registries.RECIPE_TYPE, Identifier.of("trtr", id), new RecipeType<T>() {
            public String toString() {
                return "trtr:" + id;
            }
        });
    }

    public static void init() {
        // Only for <clinit>.
    }
}

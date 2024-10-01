package com.github.cao.awa.trtr.recipe.player;

import com.github.cao.awa.trtr.recipe.handcraft.HandcraftRecipe;
import com.github.cao.awa.trtr.recipe.handcraft.HandcraftRecipeInput;

public interface HandcraftingPlayer {
    HandcraftRecipeInput trtr$handcraftInput();

    void trtr$handcraftInput(HandcraftRecipeInput input);

    HandcraftRecipe trtr$handcraftRecipe();

    void trtr$handcraftRecipe(HandcraftRecipe input);

    default HandcraftRecipeInput handcraftInput() {
        return trtr$handcraftInput();
    }

    default void handcraftInput(HandcraftRecipeInput input) {
        trtr$handcraftInput(input);
    }

    default HandcraftRecipe handcraftRecipe() {
        return trtr$handcraftRecipe();
    }

    default void handcraftRecipe(HandcraftRecipe recipe) {
        trtr$handcraftRecipe(recipe);
    }
}

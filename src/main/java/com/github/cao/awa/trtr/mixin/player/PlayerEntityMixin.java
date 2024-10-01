package com.github.cao.awa.trtr.mixin.player;

import com.github.cao.awa.trtr.recipe.handcraft.HandcraftRecipe;
import com.github.cao.awa.trtr.recipe.handcraft.HandcraftRecipeInput;
import com.github.cao.awa.trtr.recipe.player.HandcraftingPlayer;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements HandcraftingPlayer {
    @Unique
    private HandcraftRecipeInput handcraftInput;
    @Unique
    private HandcraftRecipe handcraftRecipe;

    @Override
    public HandcraftRecipeInput trtr$handcraftInput() {
        return this.handcraftInput;
    }

    @Override
    public void trtr$handcraftInput(HandcraftRecipeInput input) {
        this.handcraftInput = input;
    }

    @Override
    public HandcraftRecipe trtr$handcraftRecipe() {
        return this.handcraftRecipe;
    }

    @Override
    public void trtr$handcraftRecipe(HandcraftRecipe recipe) {
        this.handcraftRecipe = recipe;
    }
}

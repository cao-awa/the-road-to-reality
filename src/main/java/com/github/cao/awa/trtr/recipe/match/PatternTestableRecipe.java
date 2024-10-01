package com.github.cao.awa.trtr.recipe.match;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public interface PatternTestableRecipe {
    boolean isPatternMatches(World world, PlayerEntity player);
}

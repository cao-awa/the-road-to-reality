package com.github.cao.awa.trtr.registry;

import net.minecraft.recipe.Recipe;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class TrtrRegistryKeys {
    public static final RegistryKey<Registry<Recipe<?>>> ITEM_PROPERTY_INJECT = of("property/item");

    private static <T> RegistryKey<Registry<T>> of(String id) {
        return RegistryKey.ofRegistry(Identifier.of("trtr", id));
    }
}

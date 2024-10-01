package com.github.cao.awa.trtr.recipe;

import com.github.cao.awa.trtr.annotation.Clinit;
import com.github.cao.awa.trtr.codec.TrtrCodec;
import com.github.cao.awa.trtr.codec.TrtrPacketCodec;
import com.github.cao.awa.trtr.recipe.handcraft.forging.HandcraftForgingRecipe;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class TrtrRecipeSerializer {
    public static final RecipeSerializer<HandcraftForgingRecipe> HANDCRAFT_FORGING = register("handcraft_forging", TrtrCodec.HANDCRAFT_FORGING_RECIPE, TrtrPacketCodec.HANDCRAFT_FORGING_RECIPE);

    static <T extends Recipe<?>> RecipeSerializer<T> register(final String id, MapCodec<T> codec, PacketCodec<RegistryByteBuf, T> packetCodec) {
        return Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of("trtr", id), new RecipeSerializer<T>() {
            @Override
            public MapCodec<T> codec() {
                return codec;
            }

            @Override
            public PacketCodec<RegistryByteBuf, T> packetCodec() {
                return packetCodec;
            }
        });
    }

    @Clinit
    public static void clinit() {
        // Do nothing.
    }
}

package com.github.cao.awa.trtr.components;

import com.github.cao.awa.apricot.util.collection.ApricotCollectionFactor;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class TrtrComponentTypes {
    private static final Map<String, TrtrComponentType<?>> types = ApricotCollectionFactor.hashMap();

    public static final TrtrComponentType<Integer> STONE_SIZE = register(
            "stone_size",
            (builder) -> builder.codec(Codec.INT).packetCodec(PacketCodecs.INTEGER),
            JsonElement::getAsInt
    );

    public static final TrtrComponentType<Integer> SHARPNESS = register(
            "sharpness",
            (builder) -> builder.codec(Codec.INT).packetCodec(PacketCodecs.INTEGER),
            JsonElement::getAsInt
    );

    public static <T> TrtrComponentType<T> register(String id, UnaryOperator<TrtrComponentTypeBuilder<T>> builderOperator, Function<JsonElement, T> valueCreator) {
        TrtrComponentType<T> type = builderOperator.apply(new TrtrComponentTypeBuilder<T>(id).valueCreator(valueCreator)).build();
        Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of("trtr", id), type);
        types.put(id, type);
        return type;
    }

    public static void init() {

    }

    public static TrtrComponentType<?> get(String id) {
        id = reserveTrtrIdentifier(id);
        return types.get(id);
    }

    private static String reserveTrtrIdentifier(String id) {
        if (id.startsWith("trtr:")) {
            id = id.substring(5);
        }
        return id;
    }

    @SuppressWarnings("unchecked")
    public static <T> T createValue(String type, JsonElement element) {
        return (T) types.get(type).valueCreator().apply(element);
    }
}

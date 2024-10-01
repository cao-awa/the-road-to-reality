package com.github.cao.awa.trtr.components;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

public class TrtrComponentTypeBuilder<T> {
    @NotNull
    private final String type;
    @Nullable
    private Codec<T> codec;
    @Nullable
    private PacketCodec<? super RegistryByteBuf, T> packetCodec;
    private Function<JsonElement, T> valueCreator;

    public TrtrComponentTypeBuilder(@NotNull String type) {
        this.type = type;
    }

    public TrtrComponentTypeBuilder<T> codec(Codec<T> codec) {
        this.codec = codec;
        return this;
    }

    public TrtrComponentTypeBuilder<T> packetCodec(PacketCodec<? super RegistryByteBuf, T> packetCodec) {
        this.packetCodec = packetCodec;
        return this;
    }

    public TrtrComponentTypeBuilder<T> valueCreator(Function<JsonElement, T> valueCreator) {
        this.valueCreator = valueCreator;
        return this;
    }

    public TrtrComponentType<T> build() {
        PacketCodec<? super RegistryByteBuf, T> packetCodec = Objects.requireNonNullElseGet(this.packetCodec, () -> PacketCodecs.registryCodec(Objects.requireNonNull(this.codec, "Missing Codec for component")));
        return new TrtrComponentType<>(this.codec, packetCodec, valueCreator, this.type);
    }
}

package com.github.cao.awa.trtr.components;

import com.github.cao.awa.trtr.components.value.TrtrValueCreator;
import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

public record TrtrComponentType<T>(Codec<T> codec,
                                   PacketCodec<? super RegistryByteBuf, T> packetCodec,
                                   TrtrValueCreator<T> valueCreator,
                                   String type
) implements ComponentType<T> {
    public String toString() {
        return Util.registryValueToString(Registries.DATA_COMPONENT_TYPE, this);
    }

    @Override
    public @Nullable Codec<T> getCodec() {
        return this.codec;
    }

    @Override
    public PacketCodec<? super RegistryByteBuf, T> getPacketCodec() {
        return this.packetCodec;
    }
}
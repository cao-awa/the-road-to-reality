package com.github.cao.awa.trtr.pair

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryByteBuf

@JvmRecord
data class IntegerRange(val min: Int, val max: Int) {
    companion object {
        fun of(value: Int): IntegerRange {
            return IntegerRange(value, value)
        }

        fun of(min: Int, max: Int): IntegerRange {
            return IntegerRange(min, max)
        }

        @JvmStatic
        fun encode(buf: RegistryByteBuf, range: IntegerRange) {
            buf.writeInt(range.min)
            buf.writeInt(range.max)
        }

        @JvmStatic
        fun decode(buf: RegistryByteBuf): IntegerRange {
            return IntegerRange(
                buf.readInt(),
                buf.readInt()
            )
        }

        @JvmStatic
        fun forGetterMin(): RecordCodecBuilder<IntegerRange, Int> =
            Codec.INT.fieldOf("min").forGetter(IntegerRange::min)

        @JvmStatic
        fun forGetterMax(): RecordCodecBuilder<IntegerRange, Int> =
            Codec.INT.fieldOf("max").forGetter(IntegerRange::max)
    }
}

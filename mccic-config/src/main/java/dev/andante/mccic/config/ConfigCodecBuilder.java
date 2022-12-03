package dev.andante.mccic.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringIdentifiable;

import java.util.function.Function;
import java.util.function.Supplier;

public final class ConfigCodecBuilder<T extends Record> {
    private final T defaultConfig;

    public ConfigCodecBuilder(T defaultConfig) {
        this.defaultConfig = defaultConfig;
    }

    public <O> RecordCodecBuilder<T, O> create(String id, Function<T, O> getter, PrimitiveCodec<O> primitiveCodec) {
        return primitiveCodec.fieldOf(id).orElse(getter.apply(this.defaultConfig)).forGetter(getter);
    }

    public RecordCodecBuilder<T, Boolean> createBool(String id, Function<T, Boolean> getter) {
        return create(id, getter, Codec.BOOL);
    }

    public RecordCodecBuilder<T, Integer> createInt(String id, Function<T, Integer> getter) {
        return create(id, getter, Codec.INT);
    }

    public RecordCodecBuilder<T, Float> createFloat(String id, Function<T, Float> getter) {
        return create(id, getter, Codec.FLOAT);
    }

    public RecordCodecBuilder<T, Long> createLong(String id, Function<T, Long> getter) {
        return create(id, getter, Codec.LONG);
    }

    public <E extends Enum<E> & StringIdentifiable> RecordCodecBuilder<T, E> createEnum(String id, Supplier<E[]> values, Function<T, E> getter) {
        return StringIdentifiable.createCodec(values).fieldOf(id).orElse(getter.apply(this.defaultConfig)).forGetter(getter);
    }
}

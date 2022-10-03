package dev.andante.mccic.config;

import com.mojang.serialization.Codec;

public final class ConfigHolder<T extends Record> {
    private final String module;
    private final Codec<T> codec;
    private final T defaultConfig;

    private T config;

    public ConfigHolder(String module, Codec<T> codec, T defaultConfig) {
        this.module = module;
        this.codec = codec;
        this.config = this.defaultConfig = defaultConfig;
    }

    public T get() {
        return this.config;
    }

    public void set(T config) {
        this.config = config;
    }

    public String getModule() {
        return this.module;
    }

    public Codec<T> getCodec() {
        return this.codec;
    }

    public T getDefaultConfig() {
        return this.defaultConfig;
    }

    public void load() {
        ConfigHelper.load(this.module, this.codec, this.defaultConfig).ifPresent(config -> this.config = config);
    }

    public void save() {
        ConfigHelper.save(this.module, this.codec, this.config);
    }
}

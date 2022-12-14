package dev.andante.mccic.config;

import com.mojang.serialization.Codec;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class ConfigHolder<T extends Record> {
    private final String module;
    private final Codec<T> codec;
    private final T defaultConfig;
    private final List<Consumer<ConfigHolder<T>>> loadListeners, saveListeners;

    private T config;

    public ConfigHolder(String module, Codec<T> codec, T defaultConfig) {
        this.module = module;
        this.codec = codec;
        this.config = this.defaultConfig = defaultConfig;
        this.loadListeners = new ArrayList<>();
        this.saveListeners = new ArrayList<>();
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

    public ConfigHolder<T> registerSaveListener(Consumer<ConfigHolder<T>> action) {
        this.saveListeners.add(action);
        return this;
    }

    public ConfigHolder<T> registerLoadListener(Consumer<ConfigHolder<T>> action) {
        this.loadListeners.add(action);
        return this;
    }

    public void load() {
        ConfigHelper.load(this.module, this.codec).ifPresent(config -> this.config = config);
        ConfigHelper.save(this.module, this.codec, this.config);
        this.forEachLoadListener(action -> action.accept(this));
    }

    public void forEachLoadListener(Consumer<Consumer<ConfigHolder<T>>> action) {
        this.loadListeners.forEach(action);
    }

    public void save() {
        ConfigHelper.save(this.module, this.codec, this.config);
        this.forEachSaveListener(action -> action.accept(this));
    }

    public void forEachSaveListener(Consumer<Consumer<ConfigHolder<T>>> action) {
        this.saveListeners.forEach(action);
    }
}

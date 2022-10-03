package dev.andante.mccic.config;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ConfigRegistry {
    public static final ConfigRegistry INSTANCE = new ConfigRegistry();

    private final List<ConfigHolder<?>> registry = new ArrayList<>();

    public ConfigRegistry() {
    }

    public <T extends Record> ConfigHolder<T> register(ConfigHolder<T> holder) {
        this.registry.add(holder);
        return holder;
    }

    public <T extends Record> ConfigHolder<T> registerAndLoad(ConfigHolder<T> holder) {
        holder.load();
        return register(holder);
    }

    public void forEach(Consumer<ConfigHolder<?>> action) {
        this.registry.forEach(action);
    }
}

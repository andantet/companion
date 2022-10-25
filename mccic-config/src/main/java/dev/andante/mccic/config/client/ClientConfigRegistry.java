package dev.andante.mccic.config.client;

import dev.andante.mccic.config.ConfigHolder;
import dev.andante.mccic.config.ConfigRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class ClientConfigRegistry extends ConfigRegistry {
    public static final ClientConfigRegistry INSTANCE = new ClientConfigRegistry();

    private final Map<ConfigHolder<?>, Function<Screen, Screen>> screens = new HashMap<>();

    public ClientConfigRegistry() {
    }

    public <T extends Record> ConfigHolder<T> registerAndLoad(ConfigHolder<T> holder, Function<Screen, Screen> screenFactory) {
        this.screens.put(holder, screenFactory);
        return super.registerAndLoad(holder);
    }

    public void forEachScreen(BiConsumer<ConfigHolder<?>, Function<Screen, Screen>> action) {
        this.screens.forEach(action);
    }
}

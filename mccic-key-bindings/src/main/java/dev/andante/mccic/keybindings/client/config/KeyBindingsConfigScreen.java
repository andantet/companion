package dev.andante.mccic.keybindings.client.config;

import dev.andante.mccic.config.client.screen.AbstractConfigScreen;
import dev.andante.mccic.keybindings.MCCICKeyBindings;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.SimpleOption;

import java.util.List;

@Environment(EnvType.CLIENT)
public class KeyBindingsConfigScreen extends AbstractConfigScreen<KeyBindingsClientConfig> {
    public static final SimpleOption<Boolean> CONFIRM_HUB_OPTION;

    public KeyBindingsConfigScreen(Screen parent) {
        super(MCCICKeyBindings.MOD_ID, parent, KeyBindingsClientConfig.CONFIG_HOLDER);
    }

    @Override
    protected List<SimpleOption<?>> getOptions() {
        return List.of(CONFIRM_HUB_OPTION);
    }

    @Override
    public KeyBindingsClientConfig createConfig() {
        return new KeyBindingsClientConfig(CONFIRM_HUB_OPTION.getValue());
    }

    static {
        KeyBindingsClientConfig config = KeyBindingsClientConfig.getConfig();
        KeyBindingsClientConfig defaultConfig = KeyBindingsClientConfig.createDefaultConfig();
        CONFIRM_HUB_OPTION = ofBoolean(MCCICKeyBindings.MOD_ID, "confirm_hub", config.confirmHub(), defaultConfig.confirmHub());
    }
}

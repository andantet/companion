package dev.andante.mccic.toasts.client.config;

import dev.andante.mccic.config.client.screen.AbstractConfigScreen;
import dev.andante.mccic.toasts.MCCICToasts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.SimpleOption;

import java.util.List;

public class ToastsConfigScreen extends AbstractConfigScreen<ToastsClientConfig> {
    public static final SimpleOption<Boolean> QUESTS_OPTION;

    public ToastsConfigScreen(Screen parent) {
        super(MCCICToasts.MOD_ID, parent, ToastsClientConfig.CONFIG_HOLDER);
    }

    @Override
    protected List<SimpleOption<?>> getOptions() {
        return List.of(QUESTS_OPTION);
    }

    @Override
    public ToastsClientConfig createConfig() {
        return new ToastsClientConfig(QUESTS_OPTION.getValue());
    }

    static {
        ToastsClientConfig config = ToastsClientConfig.getConfig();
        ToastsClientConfig defaultConfig = ToastsClientConfig.createDefaultConfig();
        QUESTS_OPTION = ofBoolean(MCCICToasts.MOD_ID, "quests", config.quests(), defaultConfig.quests());
    }
}

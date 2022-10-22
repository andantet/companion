package dev.andante.mccic.toasts.client.config;

import dev.andante.mccic.config.client.screen.AbstractConfigScreen;
import dev.andante.mccic.toasts.MCCICToasts;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.SimpleOption;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ToastsConfigScreen extends AbstractConfigScreen<ToastsClientConfig> {
    public static final SimpleOption<Boolean> QUESTS_OPTION;
    public static final SimpleOption<Boolean> ACHIEVEMENTS_OPTION;

    public ToastsConfigScreen(Screen parent) {
        super(MCCICToasts.MOD_ID, parent, ToastsClientConfig.CONFIG_HOLDER);
    }

    @Override
    protected List<SimpleOption<?>> getOptions() {
        return List.of(QUESTS_OPTION, ACHIEVEMENTS_OPTION);
    }

    @Override
    public ToastsClientConfig createConfig() {
        return new ToastsClientConfig(QUESTS_OPTION.getValue(), ACHIEVEMENTS_OPTION.getValue());
    }

    static {
        ToastsClientConfig config = ToastsClientConfig.getConfig();
        ToastsClientConfig defaultConfig = ToastsClientConfig.createDefaultConfig();
        QUESTS_OPTION = ofBoolean(MCCICToasts.MOD_ID, "quests", config.quests(), defaultConfig.quests());
        ACHIEVEMENTS_OPTION = ofBoolean(MCCICToasts.MOD_ID, "achievements", config.achievements(), defaultConfig.achievements());
    }
}

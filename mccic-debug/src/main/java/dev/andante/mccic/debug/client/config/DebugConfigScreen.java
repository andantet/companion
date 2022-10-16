package dev.andante.mccic.debug.client.config;

import dev.andante.mccic.config.client.screen.AbstractConfigScreen;
import dev.andante.mccic.debug.MCCICDebug;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.SimpleOption;

import java.util.List;

public class DebugConfigScreen extends AbstractConfigScreen<DebugClientConfig> {
    public static final SimpleOption<Boolean> DEBUG_HUD_OPTION;
    public static final SimpleOption<Boolean> CHAT_ALL_SOUNDS_OPTION;

    public DebugConfigScreen(Screen parent) {
        super(MCCICDebug.MOD_ID, parent, DebugClientConfig.CONFIG_HOLDER);
    }

    @Override
    protected List<SimpleOption<?>> getOptions() {
        return List.of(DEBUG_HUD_OPTION, CHAT_ALL_SOUNDS_OPTION);
    }

    @Override
    public DebugClientConfig createConfig() {
        return new DebugClientConfig(DEBUG_HUD_OPTION.getValue(), CHAT_ALL_SOUNDS_OPTION.getValue());
    }

    static {
        DebugClientConfig config = DebugClientConfig.getConfig();
        DebugClientConfig defaultConfig = DebugClientConfig.createDefaultConfig();
        DEBUG_HUD_OPTION = ofBoolean(MCCICDebug.MOD_ID, "debug_hud", config.debugHud(), defaultConfig.debugHud());
        CHAT_ALL_SOUNDS_OPTION = ofBoolean(MCCICDebug.MOD_ID, "chat_all_sounds", config.chatAllSounds(), defaultConfig.chatAllSounds());
    }
}

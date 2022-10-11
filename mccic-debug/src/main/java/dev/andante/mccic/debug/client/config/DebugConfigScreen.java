package dev.andante.mccic.debug.client.config;

import dev.andante.mccic.config.client.screen.AbstractConfigScreen;
import dev.andante.mccic.debug.MCCICDebug;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.SimpleOption;

public class DebugConfigScreen extends AbstractConfigScreen<DebugClientConfig> {
    public static final SimpleOption<Boolean> DEBUG_HUD_OPTION;
    public static final SimpleOption<Boolean> CHAT_ALL_SOUNDS_OPTION;

    public DebugConfigScreen(Screen parent) {
        super(MCCICDebug.MOD_ID, parent, DebugClientConfig.CONFIG_HOLDER);
    }

    @Override
    protected void init() {
        super.init();
        this.list.addSingleOptionEntry(DEBUG_HUD_OPTION);
        this.list.addSingleOptionEntry(CHAT_ALL_SOUNDS_OPTION);
    }

    @Override
    protected void saveConfig() {
        DebugClientConfig.CONFIG_HOLDER.set(new DebugClientConfig(DEBUG_HUD_OPTION.getValue(), CHAT_ALL_SOUNDS_OPTION.getValue()));
        super.saveConfig();
    }

    static {
        DebugClientConfig config = DebugClientConfig.getConfig();
        DebugClientConfig defaultConfig = DebugClientConfig.createDefaultConfig();
        DEBUG_HUD_OPTION = ofBoolean(MCCICDebug.MOD_ID, "debug_hud", config.debugHud(), defaultConfig.debugHud());
        CHAT_ALL_SOUNDS_OPTION = ofBoolean(MCCICDebug.MOD_ID, "chat_all_sounds", config.chatAllSounds(), defaultConfig.chatAllSounds());
    }
}

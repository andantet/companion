package dev.andante.mccic.debug.client.config;

import dev.andante.mccic.config.client.screen.MCCICAbstractConfigScreen;
import dev.andante.mccic.debug.MCCICDebug;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.SimpleOption;

public class MCCICDebugConfigScreen extends MCCICAbstractConfigScreen<DebugClientConfig> {
    public static final SimpleOption<Boolean> DEBUG_HUD_OPTION;

    public MCCICDebugConfigScreen(Screen parent) {
        super(MCCICDebug.MOD_ID, parent, DebugClientConfig.CONFIG_HOLDER);
    }

    @Override
    protected void init() {
        super.init();
        this.list.addSingleOptionEntry(DEBUG_HUD_OPTION);
    }

    @Override
    protected void saveConfig() {
        DebugClientConfig.CONFIG_HOLDER.set(new DebugClientConfig(DEBUG_HUD_OPTION.getValue()));
        super.saveConfig();
    }

    static {
        DebugClientConfig config = DebugClientConfig.getConfig();
        DebugClientConfig defaultConfig = DebugClientConfig.createDefaultConfig();
        DEBUG_HUD_OPTION = ofBoolean(MCCICDebug.MOD_ID, "debug_hud", config.debugHud(), defaultConfig.debugHud());
    }
}

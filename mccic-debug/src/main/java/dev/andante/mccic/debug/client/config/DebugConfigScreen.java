package dev.andante.mccic.debug.client.config;

import dev.andante.mccic.config.client.screen.AbstractConfigScreen;
import dev.andante.mccic.debug.MCCICDebug;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.SimpleOption;

import java.util.List;

@Environment(EnvType.CLIENT)
public class DebugConfigScreen extends AbstractConfigScreen<DebugClientConfig> {
    public final SimpleOption<Boolean> debugHudOption;
    public final SimpleOption<Boolean> chatAllSoundsOption;
    public final SimpleOption<Boolean> rawChatOption;

    public DebugConfigScreen(Screen parent) {
        super(MCCICDebug.MOD_ID, parent, DebugClientConfig.CONFIG_HOLDER);
        this.debugHudOption = this.ofBoolean("debug_hud", DebugClientConfig::debugHud);
        this.chatAllSoundsOption = this.ofBoolean("chat_all_sounds", DebugClientConfig::chatAllSounds);
        this.rawChatOption = this.ofBoolean("raw_chat", DebugClientConfig::rawChat);
    }

    @Override
    protected List<SimpleOption<?>> getOptions() {
        return List.of(this.debugHudOption, this.chatAllSoundsOption, this.rawChatOption);
    }

    @Override
    public DebugClientConfig createConfig() {
        return new DebugClientConfig(this.debugHudOption.getValue(), this.chatAllSoundsOption.getValue(), this.rawChatOption.getValue());
    }

    @Override
    public DebugClientConfig getConfig() {
        return DebugClientConfig.getConfig();
    }

    @Override
    public DebugClientConfig getDefaultConfig() {
        return DebugClientConfig.createDefaultConfig();
    }
}

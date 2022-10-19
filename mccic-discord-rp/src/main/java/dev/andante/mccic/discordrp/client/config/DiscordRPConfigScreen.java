package dev.andante.mccic.discordrp.client.config;

import dev.andante.mccic.config.client.screen.AbstractConfigScreen;
import dev.andante.mccic.discordrp.MCCICDiscordRP;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.SimpleOption;

import java.util.List;

@Environment(EnvType.CLIENT)
public class DiscordRPConfigScreen extends AbstractConfigScreen<DiscordRPClientConfig> {
    public static final SimpleOption<Boolean> ENABLED_OPTION;

    public DiscordRPConfigScreen(Screen parent) {
        super(MCCICDiscordRP.MOD_ID, parent, DiscordRPClientConfig.CONFIG_HOLDER);
    }

    @Override
    protected List<SimpleOption<?>> getOptions() {
        return List.of(ENABLED_OPTION);
    }

    @Override
    public DiscordRPClientConfig createConfig() {
        DiscordRPClientConfig defaultConfig = DiscordRPClientConfig.createDefaultConfig();
        return new DiscordRPClientConfig(ENABLED_OPTION.getValue(), defaultConfig.clientId());
    }

    static {
        DiscordRPClientConfig config = DiscordRPClientConfig.getConfig();
        DiscordRPClientConfig defaultConfig = DiscordRPClientConfig.createDefaultConfig();
        ENABLED_OPTION = ofBoolean(MCCICDiscordRP.MOD_ID, "enabled", config.enabled(), defaultConfig.enabled());
    }
}

package dev.andante.mccic.discordrp.client.config;

import dev.andante.mccic.config.client.screen.MCCICAbstractConfigScreen;
import dev.andante.mccic.discordrp.MCCICDiscordRP;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.SimpleOption;

public class MCCICDiscordRPConfigScreen extends MCCICAbstractConfigScreen<DiscordRPClientConfig> {
    public static final SimpleOption<Boolean> ENABLED_OPTION;

    public MCCICDiscordRPConfigScreen(Screen parent) {
        super(MCCICDiscordRP.MOD_ID, parent, DiscordRPClientConfig.CONFIG_HOLDER);
    }

    @Override
    protected void init() {
        super.init();
        this.list.addSingleOptionEntry(ENABLED_OPTION);
    }

    @Override
    protected void saveConfig() {
        DiscordRPClientConfig defaultConfig = DiscordRPClientConfig.createDefaultConfig();
        DiscordRPClientConfig.CONFIG_HOLDER.set(new DiscordRPClientConfig(ENABLED_OPTION.getValue(), defaultConfig.clientId()));
        super.saveConfig();
    }

    static {
        DiscordRPClientConfig config = DiscordRPClientConfig.getConfig();
        DiscordRPClientConfig defaultConfig = DiscordRPClientConfig.createDefaultConfig();
        ENABLED_OPTION = ofBoolean(MCCICDiscordRP.MOD_ID, "enabled", config.enabled(), defaultConfig.enabled());
    }
}

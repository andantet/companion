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
    public static final SimpleOption<Boolean> DISPLAY_GAME_OPTION;
    public static final SimpleOption<Boolean> DISPLAY_GAME_TIME_OPTION;
    public static final SimpleOption<Boolean> DISPLAY_GAME_STATE_OPTION;
    public static final SimpleOption<Boolean> DISPLAY_QUEUE_OPTION;

    public DiscordRPConfigScreen(Screen parent) {
        super(MCCICDiscordRP.MOD_ID, parent, DiscordRPClientConfig.CONFIG_HOLDER);
    }

    @Override
    protected List<SimpleOption<?>> getOptions() {
        return List.of(ENABLED_OPTION, DISPLAY_GAME_OPTION, DISPLAY_GAME_TIME_OPTION, DISPLAY_GAME_STATE_OPTION, DISPLAY_QUEUE_OPTION);
    }

    @Override
    public DiscordRPClientConfig createConfig() {
        DiscordRPClientConfig defaultConfig = DiscordRPClientConfig.createDefaultConfig();
        return new DiscordRPClientConfig(defaultConfig.clientId(), ENABLED_OPTION.getValue(), DISPLAY_GAME_OPTION.getValue(), DISPLAY_GAME_TIME_OPTION.getValue(), DISPLAY_GAME_STATE_OPTION.getValue(), DISPLAY_QUEUE_OPTION.getValue());
    }

    static {
        DiscordRPClientConfig config = DiscordRPClientConfig.getConfig();
        DiscordRPClientConfig defaultConfig = DiscordRPClientConfig.createDefaultConfig();
        ENABLED_OPTION = ofBoolean(MCCICDiscordRP.MOD_ID, "enabled", config.enabled(), defaultConfig.enabled());
        DISPLAY_GAME_OPTION = ofBoolean(MCCICDiscordRP.MOD_ID, "display_game", config.displayGame(), defaultConfig.displayGame());
        DISPLAY_GAME_TIME_OPTION = ofBoolean(MCCICDiscordRP.MOD_ID, "display_game_time", config.displayGameTime(), defaultConfig.displayGameTime());
        DISPLAY_GAME_STATE_OPTION = ofBoolean(MCCICDiscordRP.MOD_ID, "display_game_state", config.displayGameState(), defaultConfig.displayGameState());
        DISPLAY_QUEUE_OPTION = ofBoolean(MCCICDiscordRP.MOD_ID, "display_queue", config.displayQueue(), defaultConfig.displayQueue());
    }
}

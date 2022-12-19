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
    public final SimpleOption<Boolean> enabledOption;
    public final SimpleOption<Boolean> displayGameOption;
    public final SimpleOption<Boolean> displayGameTimeOption;
    public final SimpleOption<Boolean> displayGameStateOption;
    public final SimpleOption<Boolean> displayGameArtOption;
    public final SimpleOption<Boolean> displayQueueOption;

    public DiscordRPConfigScreen(Screen parent) {
        super(MCCICDiscordRP.MOD_ID, parent, DiscordRPClientConfig.CONFIG_HOLDER);
        this.enabledOption = this.ofBoolean("enabled", DiscordRPClientConfig::enabled);
        this.displayGameOption = this.ofBoolean("display_game", DiscordRPClientConfig::displayGame);
        this.displayGameTimeOption = this.ofBoolean("display_game_time", DiscordRPClientConfig::displayGameTime);
        this.displayGameStateOption = this.ofBoolean("display_game_state", DiscordRPClientConfig::displayGameState);
        this.displayGameArtOption = this.ofBooleanTooltip("display_game_art", DiscordRPClientConfig::displayGameArt);
        this.displayQueueOption = this.ofBoolean("display_queue", DiscordRPClientConfig::displayQueue);
    }

    @Override
    protected List<SimpleOption<?>> getOptions() {
        return List.of(this.enabledOption, this.displayGameOption, this.displayGameTimeOption, this.displayGameStateOption, this.displayGameArtOption, this.displayQueueOption);
    }

    @Override
    public DiscordRPClientConfig createConfig() {
        DiscordRPClientConfig defaultConfig = DiscordRPClientConfig.createDefaultConfig();
        return new DiscordRPClientConfig(defaultConfig.clientId(), this.enabledOption.getValue(), this.displayGameOption.getValue(), this.displayGameTimeOption.getValue(), this.displayGameStateOption.getValue(), this.displayGameArtOption.getValue(), this.displayQueueOption.getValue());
    }

    @Override
    public DiscordRPClientConfig getConfig() {
        return DiscordRPClientConfig.getConfig();
    }

    @Override
    public DiscordRPClientConfig getDefaultConfig() {
        return DiscordRPClientConfig.createDefaultConfig();
    }
}

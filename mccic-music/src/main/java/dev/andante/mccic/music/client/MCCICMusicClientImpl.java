package dev.andante.mccic.music.client;

import dev.andante.mccic.api.client.game.GameTracker;
import dev.andante.mccic.config.client.ClientConfigRegistry;
import dev.andante.mccic.config.client.command.MCCICConfigCommand;
import dev.andante.mccic.music.MCCICMusic;
import dev.andante.mccic.music.client.config.MCCICMusicConfigScreen;
import dev.andante.mccic.music.client.config.MusicClientConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class MCCICMusicClientImpl implements MCCICMusic, ClientModInitializer {
    public static final MusicTracker MUSIC_TRACKER = new MusicTracker(GameTracker.INSTANCE);

    @Override
    public void onInitializeClient() {
        ClientConfigRegistry.INSTANCE.registerAndLoad(MusicClientConfig.CONFIG_HOLDER);
        MCCICConfigCommand.registerNewConfig(ID, MCCICMusicConfigScreen::new);
    }
}

package dev.andante.mccic.music.client;

import dev.andante.mccic.api.MCCIC;
import dev.andante.mccic.api.client.game.GameTracker;
import dev.andante.mccic.config.client.ClientConfigRegistry;
import dev.andante.mccic.music.client.config.MusicClientConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class MCCICMusicClientImpl implements MCCIC, ClientModInitializer {
    public static final MusicTracker MUSIC_TRACKER = new MusicTracker(GameTracker.INSTANCE);

    @Override
    public void onInitializeClient() {
        ClientConfigRegistry.INSTANCE.registerAndLoad(MusicClientConfig.CONFIG_HOLDER);
    }
}

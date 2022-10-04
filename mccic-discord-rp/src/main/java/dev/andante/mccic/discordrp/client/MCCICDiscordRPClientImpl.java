package dev.andante.mccic.discordrp.client;

import dev.andante.mccic.api.MCCIC;
import dev.andante.mccic.config.client.ClientConfigRegistry;
import dev.andante.mccic.discordrp.client.config.DiscordRPClientConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class MCCICDiscordRPClientImpl implements MCCIC, ClientModInitializer {
    public static final MCCIDiscordRichPresenceManager PRESENCE = new MCCIDiscordRichPresenceManager();

    @Override
    public void onInitializeClient() {
        ClientConfigRegistry.INSTANCE.registerAndLoad(DiscordRPClientConfig.CONFIG_HOLDER);
    }
}

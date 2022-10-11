package dev.andante.mccic.discordrp.client;

import dev.andante.mccic.config.client.ClientConfigRegistry;
import dev.andante.mccic.config.client.command.MCCICConfigCommand;
import dev.andante.mccic.discordrp.MCCICDiscordRP;
import dev.andante.mccic.discordrp.client.config.DiscordRPClientConfig;
import dev.andante.mccic.discordrp.client.config.DiscordRPConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class MCCICDiscordRPClientImpl implements MCCICDiscordRP, ClientModInitializer {
    public static final DiscordRichPresenceManager PRESENCE = new DiscordRichPresenceManager();

    @Override
    public void onInitializeClient() {
        ClientConfigRegistry.INSTANCE.registerAndLoad(DiscordRPClientConfig.CONFIG_HOLDER);
        MCCICConfigCommand.registerNewConfig(ID, DiscordRPConfigScreen::new);
    }
}

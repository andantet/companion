package dev.andante.mccic.discordrp.client;

import dev.andante.mccic.api.client.game.GameTracker;
import dev.andante.mccic.discordrp.client.config.DiscordRPClientConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

/**
 * Holds and manages an instance of {@link MCCIDiscordRichPresence}.
 */
public class MCCIDiscordRichPresenceManager {
    private MCCIDiscordRichPresence client;

    public MCCIDiscordRichPresenceManager() {
        ClientTickEvents.END_CLIENT_TICK.register(clientx -> this.update());
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    protected void tryConnect() {
        if (this.client == null) {
            this.client = new MCCIDiscordRichPresence(DiscordRPClientConfig.getConfig().clientId());
            this.client.tryConnect();
        }
    }

    protected void tryDisconnect() {
        if (this.client != null) {
            this.client.tryDisconnect();
            this.client = null;
        }
    }

    protected void update() {
        DiscordRPClientConfig config = DiscordRPClientConfig.getConfig();
        if (config.enabled()) {
            GameTracker tracker = GameTracker.INSTANCE;
            if (tracker.isOnServer()) {
                this.tryConnect();
                return;
            }
        }

        this.tryDisconnect();
    }

    protected void shutdown() {
        this.tryDisconnect();
    }
}

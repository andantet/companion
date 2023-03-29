package dev.andante.mccic.discordrp.client;

import dev.andante.mccic.api.client.tracker.GameTracker;
import dev.andante.mccic.discordrp.client.config.DiscordRPClientConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

/**
 * Holds and manages an instance of {@link DiscordRichPresence}.
 */
@Environment(EnvType.CLIENT)
public class DiscordRichPresenceManager {
    private DiscordRichPresence client;

    public DiscordRichPresenceManager() {
        ClientTickEvents.END_CLIENT_TICK.register(clientx -> this.update());
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    protected void tryConnect() {
        if (this.client == null) {
            try {
                this.client = new DiscordRichPresence(DiscordRPClientConfig.getConfig().clientId());
                this.client.tryConnect();
            } catch (Exception exception) {
                DiscordRichPresence.LOGGER.error("Unable to connect to the Discord client", exception);
                this.client = null;
            }
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

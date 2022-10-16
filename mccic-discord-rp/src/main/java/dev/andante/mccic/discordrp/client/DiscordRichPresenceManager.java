package dev.andante.mccic.discordrp.client;

import dev.andante.mccic.api.client.event.MCCIGameEvents;
import dev.andante.mccic.api.client.game.GameTracker;
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
        MCCIGameEvents.STATE_UPDATE.register(this::updatePresence);
        MCCIGameEvents.GAME_CHANGE.register(this::updatePresence);
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    public void updatePresence(Object obj1, Object obj2) {
        if (this.client != null) {
            this.client.update();
        }
    }

    protected void tryConnect() {
        if (this.client == null) {
            this.client = new DiscordRichPresence(DiscordRPClientConfig.getConfig().clientId());
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

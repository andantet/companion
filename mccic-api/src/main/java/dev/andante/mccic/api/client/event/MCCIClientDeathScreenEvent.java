package dev.andante.mccic.api.client.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.screen.DeathScreen;

@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface MCCIClientDeathScreenEvent {
    /**
     * Invoked after the client requests a respawn from the server.
     */
    Event<MCCIClientDeathScreenEvent> EVENT = EventFactory.createArrayBacked(MCCIClientDeathScreenEvent.class, callbacks -> screen -> {
        for (MCCIClientDeathScreenEvent callback : callbacks) {
            callback.onDeathScreen(screen);
        }
    });

    void onDeathScreen(DeathScreen screen);
}

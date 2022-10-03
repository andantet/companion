package dev.andante.mccic.api.client.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.network.ClientPlayerEntity;

@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface MCCIClientRespawnEvent {
    /**
     * Invoked after the client requests a respawn from the server.
     */
    Event<MCCIClientRespawnEvent> EVENT = EventFactory.createArrayBacked(MCCIClientRespawnEvent.class, callbacks -> player -> {
        for (MCCIClientRespawnEvent callback : callbacks) {
            callback.onRespawn(player);
        }
    });

    void onRespawn(ClientPlayerEntity player);
}

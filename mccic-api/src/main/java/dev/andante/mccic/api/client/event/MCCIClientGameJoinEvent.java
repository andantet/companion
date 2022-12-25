package dev.andante.mccic.api.client.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;

@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface MCCIClientGameJoinEvent {
    /**
     * Invoked after a client receives a game join packet from the server.
     */
    Event<MCCIClientGameJoinEvent> EVENT = EventFactory.createArrayBacked(MCCIClientGameJoinEvent.class, callbacks -> (handler, packet) -> {
        for (MCCIClientGameJoinEvent callback : callbacks) {
            callback.onClientGameJoin(handler, packet);
        }
    });

    void onClientGameJoin(ClientPlayNetworkHandler handler, GameJoinS2CPacket packet);
}

package dev.andante.mccic.api.client.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.packet.s2c.login.LoginHelloS2CPacket;

@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface MCCIClientLoginHelloEvent {
    /**
     * Invoked when a client receives the login 'hello' packet from the server.
     */
    Event<MCCIClientLoginHelloEvent> EVENT = EventFactory.createArrayBacked(MCCIClientLoginHelloEvent.class, callbacks -> (handler, packet) -> {
        for (MCCIClientLoginHelloEvent callback : callbacks) {
            callback.onClientLoginHello(handler, packet);
        }
    });

    void onClientLoginHello(ClientLoginNetworkHandler handler, LoginHelloS2CPacket packet);
}

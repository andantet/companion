package dev.andante.mccic.api.client.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;

@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface ClientLoginSuccessEvent {
    /**
     * Invoked when a client receives the login 'success' packet from the server.
     */
    Event<ClientLoginSuccessEvent> EVENT = EventFactory.createArrayBacked(ClientLoginSuccessEvent.class, callbacks -> (handler, packet) -> {
        for (ClientLoginSuccessEvent callback : callbacks) {
            callback.onClientLoginSuccess(handler, packet);
        }
    });

    void onClientLoginSuccess(ClientLoginNetworkHandler handler, LoginSuccessS2CPacket packet);
}

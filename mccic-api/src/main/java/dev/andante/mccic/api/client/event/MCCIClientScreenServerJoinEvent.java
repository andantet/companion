package dev.andante.mccic.api.client.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface MCCIClientScreenServerJoinEvent {
    /**
     * Invoked when a client joins a server from the multiplayer screen.
     */
    Event<MCCIClientScreenServerJoinEvent> EVENT = EventFactory.createArrayBacked(MCCIClientScreenServerJoinEvent.class, callbacks -> (screen, client, address, info) -> {
        for (MCCIClientScreenServerJoinEvent callback : callbacks) {
            callback.onClientScreenServerJoin(screen, client, address, info);
        }
    });

    void onClientScreenServerJoin(Screen screen, MinecraftClient client, ServerAddress address, @Nullable ServerInfo info);
}

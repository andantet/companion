package dev.andante.mccic.keybindings.client;

import com.google.common.reflect.Reflection;
import dev.andante.mccic.api.client.tracker.ChatModeTracker;
import dev.andante.mccic.api.client.tracker.QueueTracker;
import dev.andante.mccic.keybindings.MCCICKeyBindings;
import dev.andante.mccic.toasts.client.toast.ErrorToast;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public final class MCCICKeyBindingsClientImpl implements MCCICKeyBindings, ClientModInitializer {
    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void onInitializeClient() {
        Reflection.initialize(MCCICKeyBindingsRegistry.class);

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (MCCICKeyBindingsRegistry.TOGGLE_CHAT_MODE.wasPressed()) {
                ChatModeTracker.INSTANCE.switchToNext(client);
            } else if (MCCICKeyBindingsRegistry.LEAVE_QUEUE.wasPressed()) {
                if (!QueueTracker.INSTANCE.leaveQueue() && FabricLoader.getInstance().isModLoaded("mccic-toasts")) {
                    new ErrorToast(Text.translatable("ui.mccic-key-bindings.not_in_queue")).add();
                }
            }
        });
    }
}

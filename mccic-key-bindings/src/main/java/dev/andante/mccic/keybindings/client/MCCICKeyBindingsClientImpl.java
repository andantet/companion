package dev.andante.mccic.keybindings.client;

import com.google.common.reflect.Reflection;
import dev.andante.mccic.api.client.tracker.ChatModeTracker;
import dev.andante.mccic.api.client.tracker.GameTracker;
import dev.andante.mccic.api.client.util.SoundFactory;
import dev.andante.mccic.api.sound.MCCICSounds;
import dev.andante.mccic.keybindings.MCCICKeyBindings;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public final class MCCICKeyBindingsClientImpl implements MCCICKeyBindings, ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Reflection.initialize(MCCICKeyBindingsRegistry.class);

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (GameTracker.INSTANCE.isOnServer()) {
                if (MCCICKeyBindingsRegistry.TOGGLE_CHAT_MODE.wasPressed()) {
                    if (ChatModeTracker.INSTANCE.switchToNext(client)) {
                        client.getSoundManager().play(SoundFactory.create(MCCICSounds.UI_CLICK_NORMAL));
                    } else {
                        client.player.sendMessage(Text.translatable("text.%s.toggle_chat_mode.no_chat_modes_available".formatted(MCCICKeyBindings.MOD_ID)).formatted(Formatting.RED));
                    }
                }
            }
        });
    }
}

package dev.andante.mccic.api.client.event;

import dev.andante.mccic.api.event.EventResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface MCCIChatEvent {
    /**
     * Invoked before a chat message is added to chat.
     */
    Event<MCCIChatEvent> EVENT = EventFactory.createArrayBacked(MCCIChatEvent.class, callbacks -> (chatHud, message, raw, signature, ticks, indicator, refresh) -> {
        for (MCCIChatEvent callback : callbacks) {
            EventResult result = callback.onChatEvent(chatHud, message, raw, signature, ticks, indicator, refresh);
            if (result.interruptsFurtherEvaluation()) {
                return result;
            }
        }

        return EventResult.pass();
    });

    /**
     * @return a {@link EventResult} - chat message will not be added when interrupting
     */
    EventResult onChatEvent(ChatHud chatHud, Text message, String raw, @Nullable MessageSignatureData signature, int ticks, @Nullable MessageIndicator indicator, boolean refresh);
}

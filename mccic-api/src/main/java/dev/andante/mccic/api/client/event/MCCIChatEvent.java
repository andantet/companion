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
import net.minecraft.text.TextColor;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface MCCIChatEvent {
    /**
     * Invoked before a chat message is added to chat.
     */
    Event<MCCIChatEvent> EVENT = EventFactory.createArrayBacked(MCCIChatEvent.class, callbacks -> context -> {
        boolean cancels = false;

        for (MCCIChatEvent callback : callbacks) {
            EventResult result = callback.onChatEvent(context);
            cancels = cancels || result.isFalse();
            if (result.interruptsFurtherEvaluation()) {
                return result;
            }
        }

        return cancels ? EventResult.cancel() : EventResult.pass();
    });

    /**
     * @return a {@link EventResult} - chat message will not be added when interrupting
     */
    EventResult onChatEvent(Context context);

    record Context(ChatHud chatHud, Text message, @Nullable MessageSignatureData signature, int ticks, @Nullable MessageIndicator indicator, boolean refresh) {
        public String getRaw() {
            return message.getString();
        }

        public boolean isEmote() {
            TextColor textColor = message.getStyle().getColor();
            if (textColor != null) {
                int color = textColor.getRgb();
                return color == 0xFF7E40;
            }

            return false;
        }
    }
}

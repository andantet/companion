package dev.andante.mccic.api.client.tracker;

import dev.andante.mccic.api.client.UnicodeIconsStore;
import dev.andante.mccic.api.client.UnicodeIconsStore.Icon;
import dev.andante.mccic.api.client.util.ClientHelper;
import dev.andante.mccic.api.util.ChatMode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

/**
 * Tracks the current {@link ChatMode}.
 */
@Environment(EnvType.CLIENT)
public class ChatModeTracker {
    public static final ChatModeTracker INSTANCE = new ChatModeTracker();

    private ChatMode chatMode;

    public ChatModeTracker() {
        this.chatMode = ChatMode.LOCAL;
        ClientTickEvents.END_CLIENT_TICK.register(this::tick);
    }

    private void tick(MinecraftClient client) {
        Text text = ClientHelper.getActionBarText();
        if (text != null) {
            this.chatMode = UnicodeIconsStore.doesTextContainIconExact(text, Icon.CHAT_PARTY) ? ChatMode.PARTY : ChatMode.LOCAL;
        } else {
            this.chatMode = ChatMode.LOCAL;
        }
    }

    public ChatMode getChatMode() {
        return this.chatMode;
    }

    public ChatMode switchToNext(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        ChatMode next = this.chatMode.next();
        player.sendCommand("chat %s".formatted(next.getId()), null);
        return next;
    }
}

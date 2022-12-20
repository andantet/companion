package dev.andante.mccic.api.client.tracker;

import dev.andante.mccic.api.client.UnicodeIconsStore;
import dev.andante.mccic.api.client.UnicodeIconsStore.Icon;
import dev.andante.mccic.api.client.util.ClientHelper;
import dev.andante.mccic.api.game.Game;
import dev.andante.mccic.api.util.ChatMode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

/**
 * Tracks the current {@link ChatMode}.
 */
@Environment(EnvType.CLIENT)
public class ChatModeTracker {
    public static final ChatModeTracker INSTANCE = new ChatModeTracker();

    /**
     * @implNote {@link ChatMode} is not a client class, {@link Icon} is.
     */
    public static final Map<Icon, ChatMode> CHAT_MODE_ICONS = Map.of(
            Icon.CHAT_LOCAL, ChatMode.LOCAL,
            Icon.CHAT_PARTY, ChatMode.PARTY,
            Icon.CHAT_TEAM, ChatMode.TEAM
    );

    public static final Map<ChatMode, BooleanSupplier> CHAT_MODE_PREDICATES = Map.of(
            ChatMode.PARTY, PartyTracker.INSTANCE::isInParty,
            ChatMode.TEAM, () -> GameTracker.INSTANCE.getGame().filter(Game::hasTeamChat).isPresent()
    );

    private ChatMode chatMode;

    public ChatModeTracker() {
        this.chatMode = ChatMode.LOCAL;
        ClientTickEvents.END_CLIENT_TICK.register(this::tick);
    }

    private void tick(MinecraftClient client) {
        this.chatMode = ChatMode.LOCAL;

        Text text = ClientHelper.getActionBarText();
        if (text != null) {
            for (Icon icon : CHAT_MODE_ICONS.keySet()) {
                if (UnicodeIconsStore.doesTextContainIconExact(text, icon)) {
                    this.chatMode = CHAT_MODE_ICONS.get(icon);
                    break;
                }
            }
        }
    }

    public ChatMode getChatMode() {
        return this.chatMode;
    }

    public boolean switchTo(MinecraftClient client, ChatMode mode) {
        if (mode != this.chatMode) {
            ClientPlayerEntity player = client.player;
            player.networkHandler.sendCommand("chat %s".formatted(mode.getId()));
            return true;
        }

        return false;
    }

    public boolean switchToNext(MinecraftClient client) {
        return this.switchTo(client, this.getNext());
    }

    public ChatMode getNext(ChatMode mode) {
        List<ChatMode> values = this.getAvailableModes();
        return values.get((values.indexOf(mode) + 1) % values.size());
    }

    public ChatMode getNext() {
        return this.getNext(this.chatMode);
    }

    public List<ChatMode> getAvailableModes() {
        return Arrays.stream(ChatMode.values()).filter(xmode -> !CHAT_MODE_PREDICATES.containsKey(xmode) || CHAT_MODE_PREDICATES.get(xmode).getAsBoolean()).toList();
    }
}

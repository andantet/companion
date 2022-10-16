package dev.andante.mccic.toasts.client;

import dev.andante.mccic.api.client.UnicodeIconsStore;
import dev.andante.mccic.api.client.UnicodeIconsStore.Icon;
import dev.andante.mccic.api.client.event.MCCIChatEvent;
import dev.andante.mccic.api.client.toast.CustomToast;
import dev.andante.mccic.api.event.EventResult;
import dev.andante.mccic.config.client.ClientConfigRegistry;
import dev.andante.mccic.config.client.command.MCCICConfigCommand;
import dev.andante.mccic.toasts.MCCICToasts;
import dev.andante.mccic.toasts.client.config.ToastsClientConfig;
import dev.andante.mccic.toasts.client.config.ToastsConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public final class MCCICToastsClientImpl implements MCCICToasts, ClientModInitializer {
    public static final Identifier QUEST_TOAST_TEXTURE = new Identifier(MOD_ID, "textures/gui/toasts/quests.png");
    public static final String QUEST_COMPLETE_TEXT = "(X) Quest complete: ";

    @Override
    public void onInitializeClient() {
        ClientConfigRegistry.INSTANCE.registerAndLoad(ToastsClientConfig.CONFIG_HOLDER);
        MCCICConfigCommand.registerNewConfig(ID, ToastsConfigScreen::new);
        MCCIChatEvent.EVENT.register(this::onChatEvent);
    }

    public EventResult onChatEvent(ChatHud chatHud, Text message, String raw, @Nullable MessageSignatureData signature, int ticks, @Nullable MessageIndicator indicator, boolean refresh) {
        ToastsClientConfig config = ToastsClientConfig.getConfig();

        if (config.quests()) {
            if (raw.startsWith("(") && Objects.equals(raw.charAt(1), UnicodeIconsStore.INSTANCE.getCharacterFor(Icon.QUEST_BOOK))) {
                String name = raw.substring(QUEST_COMPLETE_TEXT.length());
                new CustomToast(
                    Text.translatable("toast.%s.quest_complete.title".formatted(MOD_ID)),
                    Text.translatable("toast.%s.quest_complete.description".formatted(MOD_ID), name),
                    QUEST_TOAST_TEXTURE
                ).add();
                return EventResult.cancel();
            }
        }

        return EventResult.pass();
    }
}

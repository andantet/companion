package dev.andante.mccic.chat.mixin.client;

import dev.andante.mccic.api.client.tracker.GameTracker;
import dev.andante.mccic.api.util.TextQuery;
import dev.andante.mccic.chat.client.ChatHudLineVisibleAccess;
import dev.andante.mccic.chat.client.MCCICChatClientImpl;
import dev.andante.mccic.chat.client.config.ChatClientConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.client.util.Session;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Adds the relevant data used in {@link ChatHudLineVisibleMixin}.
 * @see ChatHudLineVisibleMixin
 * @see ChatHudLineVisibleAccess
 */
@Environment(EnvType.CLIENT)
@Mixin(ChatHud.class)
public class ChatHudMixin {
    @Shadow @Final private MinecraftClient client;
    @Shadow @Final private List<ChatHudLine.Visible> visibleMessages;

    @Inject(
        method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;add(ILjava/lang/Object;)V",
            ordinal = 0,
            shift = At.Shift.AFTER
        ),
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void onAddMessage(Text message, @Nullable MessageSignatureData signature, int ticks, @Nullable MessageIndicator indicator, boolean refresh, CallbackInfo ci, int scale, List<OrderedText> orderedList, boolean focused, int i) {
        if (GameTracker.INSTANCE.isOnServer()) {
            ChatHudLine.Visible visible = this.visibleMessages.get(0);

            Text modified = message.copy();
            Session session = this.client.getSession();
            String username = session.getUsername();

            Pattern pattern = Pattern.compile("(.*)(" + username + ")(.*)", Pattern.CASE_INSENSITIVE);
            TextQuery.findTexts(modified, textx -> {
                if (textx.getContent() instanceof LiteralTextContent content) {
                    String raw = content.string();
                    return pattern.matcher(raw).find();
                }

                return false;
            }).forEach(query -> MCCICChatClientImpl.replaceAndHighlightRegex(query.getResult(), pattern, ChatClientConfig.getConfig().mentionsColor()));

            List<OrderedText> modifiedOrdered = ChatMessages.breakRenderedChatMessageLines(modified, scale, this.client.textRenderer);
            visible.mccic_setMentionedText(modifiedOrdered.get(i));
        }

    }
}

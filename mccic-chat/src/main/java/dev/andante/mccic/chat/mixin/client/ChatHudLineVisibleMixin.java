package dev.andante.mccic.chat.mixin.client;

import dev.andante.mccic.api.client.tracker.GameTracker;
import dev.andante.mccic.chat.client.ChatHudLineVisibleAccess;
import dev.andante.mccic.chat.client.config.ChatClientConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Adds extra data to {@link net.minecraft.client.gui.hud.ChatHudLine.Visible} to enable live-toggling mentions.
 * @see ChatHudLineVisibleMixin
 * @see ChatHudLineVisibleAccess
 */
@Environment(EnvType.CLIENT)
@Mixin(ChatHudLine.Visible.class)
public class ChatHudLineVisibleMixin implements ChatHudLineVisibleAccess {
    private OrderedText mccic_mentionedText;

    @Inject(method = "content", at = @At("HEAD"), cancellable = true)
    private void onGetContent(CallbackInfoReturnable<OrderedText> cir) {
        if (GameTracker.INSTANCE.isOnServer()) {
            if (ChatClientConfig.getConfig().mentions()) {
                cir.setReturnValue(this.mccic_mentionedText);
            }
        }
    }

    @Unique
    @Override
    public void mccic_setMentionedText(OrderedText text) {
        this.mccic_mentionedText = text;
    }
}

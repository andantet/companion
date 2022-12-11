package dev.andante.mccic.api.mixin.client;

import dev.andante.mccic.api.client.event.MCCIChatEvent;
import dev.andante.mccic.api.client.tracker.GameTracker;
import dev.andante.mccic.api.event.EventResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ChatHud.class)
public class ChatHudMixin {
    @Inject(
            method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onChatMessage(Text message, MessageSignatureData signature, int ticks, MessageIndicator indicator, boolean refresh, CallbackInfo ci) {
        GameTracker tracker = GameTracker.INSTANCE;
        if (tracker.isOnServer()) {
            EventResult result = MCCIChatEvent.EVENT.invoker().onChatEvent(new MCCIChatEvent.Context((ChatHud) (Object) this, message, signature, ticks, indicator, refresh));
            if (result.isFalse()) {
                ci.cancel();
            }
        }
    }
}

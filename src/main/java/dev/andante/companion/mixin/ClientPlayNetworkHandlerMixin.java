package dev.andante.companion.mixin;

import dev.andante.companion.api.event.TitleEvents;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    /**
     * Hands off the tail of onTitle to an event.
     */
    @Inject(method = "onTitle", at = @At("TAIL"))
    private void onOnTitle(TitleS2CPacket packet, CallbackInfo ci) {
        TitleEvents.INSTANCE.getTITLE().invoker().onTitle(packet.getTitle());
    }

    /**
     * Hands off the tail of onSubtitle to an event.
     */
    @Inject(method = "onSubtitle", at = @At("TAIL"))
    private void onOnSubtitle(SubtitleS2CPacket packet, CallbackInfo ci) {
        TitleEvents.INSTANCE.getSUBTITLE().invoker().onTitle(packet.getSubtitle());
    }
}

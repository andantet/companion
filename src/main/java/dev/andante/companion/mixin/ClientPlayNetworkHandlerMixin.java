package dev.andante.companion.mixin;

import dev.andante.companion.event.PacketEvents;
import dev.andante.companion.event.TitleEvents;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.Packet;
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

    /**
     * Hands off the tail of sendPacket to an event.
     */
    @Inject(method = "sendPacket(Lnet/minecraft/network/packet/Packet;)V", at = @At("TAIL"))
    private void onSendPacket(Packet<?> packet, CallbackInfo ci) {
        PacketEvents.INSTANCE.getOUT().invoker().onPacket(packet);
    }
}

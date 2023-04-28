package dev.andante.mccic.hud.mixin.client;

import dev.andante.mccic.api.client.tracker.GameTracker;
import dev.andante.mccic.hud.client.config.HudClientConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ServerMetadataS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    /**
     * Hides the 'Chat messages can't be verified' toast if configured.
     */
    @Inject(
            method = "onServerMetadata",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/packet/s2c/play/ServerMetadataS2CPacket;isSecureChatEnforced()Z",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void onOnServerMetadata(ServerMetadataS2CPacket packet, CallbackInfo ci) {
        if (GameTracker.INSTANCE.isOnServer() && HudClientConfig.getConfig().hideSecureChatEnforcementToast()) {
            ci.cancel();
        }
    }
}

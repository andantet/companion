package dev.andante.mccic.api.mixin.client;

import dev.andante.mccic.api.client.event.MCCIClientGameJoinEvent;
import dev.andante.mccic.api.client.tracker.GameTracker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    /**
     * Suppress 'Received passengers for unknown entity' logs.
     */
    @Inject(
        method = "onEntityPassengersSet",
        at = @At(
            value = "INVOKE",
            target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;)V",
            shift = At.Shift.BEFORE,
            remap = false
        ),
        cancellable = true
    )
    private void onPassengerUnknownLog(EntityPassengersSetS2CPacket packet, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "onGameJoin", at = @At("TAIL"))
    private void onOnGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
        if (GameTracker.INSTANCE.isOnServer()) {
            MCCIClientGameJoinEvent.EVENT.invoker().onClientGameJoin((ClientPlayNetworkHandler) (Object) this, packet);
        }
    }
}

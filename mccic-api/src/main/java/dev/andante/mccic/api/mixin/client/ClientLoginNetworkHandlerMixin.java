package dev.andante.mccic.api.mixin.client;

import dev.andante.mccic.api.client.event.MCCIClientLoginHelloEvent;
import dev.andante.mccic.api.client.tracker.GameTracker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.packet.s2c.login.LoginHelloS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientLoginNetworkHandler.class)
public class ClientLoginNetworkHandlerMixin {
    @Inject(method = "onHello", at = @At("TAIL"))
    private void onOnHello(LoginHelloS2CPacket packet, CallbackInfo ci) {
        if (GameTracker.INSTANCE.isOnServer()) {
            MCCIClientLoginHelloEvent.EVENT.invoker().onClientLoginHello((ClientLoginNetworkHandler) (Object) this, packet);
        }
    }
}

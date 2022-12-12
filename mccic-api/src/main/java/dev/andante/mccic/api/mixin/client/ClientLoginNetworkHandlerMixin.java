package dev.andante.mccic.api.mixin.client;

import dev.andante.mccic.api.client.event.ClientLoginSuccessEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientLoginNetworkHandler.class)
public class ClientLoginNetworkHandlerMixin {
    @Inject(method = "onSuccess", at = @At("TAIL"))
    private void onOnSuccess(LoginSuccessS2CPacket packet, CallbackInfo ci) {
        ClientLoginSuccessEvent.EVENT.invoker().onClientLoginSuccess((ClientLoginNetworkHandler) (Object) this, packet);
    }
}

package dev.andante.mccic.api.mixin.client;

import dev.andante.mccic.api.client.event.MCCIClientRespawnEvent;
import dev.andante.mccic.api.client.game.GameTracker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
    @Inject(method = "requestRespawn", at = @At("TAIL"))
    private void onRequestRespawn(CallbackInfo ci) {
        if (GameTracker.INSTANCE.isOnServer()) {
            MCCIClientRespawnEvent.EVENT.invoker().onRespawn((ClientPlayerEntity) (Object) this);
        }
    }
}

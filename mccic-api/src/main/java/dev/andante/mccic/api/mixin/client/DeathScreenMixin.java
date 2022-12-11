package dev.andante.mccic.api.mixin.client;

import dev.andante.mccic.api.client.event.MCCIClientDeathScreenEvent;
import dev.andante.mccic.api.client.tracker.GameTracker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.DeathScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(DeathScreen.class)
public abstract class DeathScreenMixin {
    @Inject(method = "init", at = @At("TAIL"))
    private void onRequestRespawn(CallbackInfo ci) {
        if (GameTracker.INSTANCE.isOnServer()) {
            MCCIClientDeathScreenEvent.EVENT.invoker().onDeathScreen((DeathScreen) (Object) this);
        }
    }
}

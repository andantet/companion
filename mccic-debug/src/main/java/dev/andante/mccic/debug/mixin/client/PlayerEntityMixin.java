package dev.andante.mccic.debug.mixin.client;

import dev.andante.mccic.api.client.tracker.GameTracker;
import dev.andante.mccic.debug.client.config.DebugClientConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    private void onGetDisplayName(CallbackInfoReturnable<Text> cir) {
        if (GameTracker.INSTANCE.isOnServer()) {
            if (DebugClientConfig.getConfig().displayNameSuffix()) {
                cir.setReturnValue(cir.getReturnValue().copy().append(Text.of(" (DN)")));
            }
        }
    }
}

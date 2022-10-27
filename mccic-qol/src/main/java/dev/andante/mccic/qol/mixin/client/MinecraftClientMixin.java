package dev.andante.mccic.qol.mixin.client;

import dev.andante.mccic.api.client.game.GameTracker;
import dev.andante.mccic.qol.client.config.QoLClientConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "hasOutline", at = @At("HEAD"), cancellable = true)
    private void onHasOutline(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!GameTracker.INSTANCE.isOnServer()) {
            return;
        }

        switch (QoLClientConfig.getConfig().glowingMode()) {
            case DISABLED -> cir.setReturnValue(false);
            case DISABLED_FOR_PLAYERS -> {
                if (entity instanceof PlayerEntity) {
                    cir.setReturnValue(false);
                }
            }
        }
    }
}

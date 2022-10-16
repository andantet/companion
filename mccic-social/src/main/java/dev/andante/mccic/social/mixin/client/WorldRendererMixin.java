package dev.andante.mccic.social.mixin.client;

import dev.andante.mccic.api.client.game.GameTracker;
import dev.andante.mccic.api.game.GameState;
import dev.andante.mccic.social.client.MCCICSocialClientImpl;
import dev.andante.mccic.social.client.config.HubPlayerRenderMode;
import dev.andante.mccic.social.client.config.SocialClientConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Shadow @Final private MinecraftClient client;

    /**
     * Modify other player rendering from configuration.
     */
    @Inject(method = "renderEntity", at = @At("HEAD"), cancellable = true)
    private void onRenderEntity(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertices, CallbackInfo ci) {
        if (GameTracker.INSTANCE.getGameState() != GameState.NONE) {
            return;
        }

        if (entity instanceof OtherClientPlayerEntity player) {
            if (MCCICSocialClientImpl.isPlayerInPlayerList(this.client, player)) {
                if (SocialClientConfig.getConfig().hubPlayerRenderMode() == HubPlayerRenderMode.INVISIBLE) {
                    ci.cancel();
                }
            }
        }
    }
}

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
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    /**
     * Modify other player rendering from configuration.
     */
    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/entity/EntityRenderer;render(Lnet/minecraft/entity/Entity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            shift = At.Shift.BEFORE
        )
    )
    private <E extends Entity> void onRender(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertices, int light, CallbackInfo ci) {
        if (GameTracker.INSTANCE.getGameState() != GameState.NONE) {
            return;
        }

        if (entity instanceof OtherClientPlayerEntity player) {
            if (MCCICSocialClientImpl.isPlayerInPlayerList(MinecraftClient.getInstance(), player)) {
                if (SocialClientConfig.getConfig().hubPlayerRenderMode() == HubPlayerRenderMode.SMALL) {
                    matrices.scale(0.5F, 0.5F, 0.5F);
                }
            }
        }
    }
}

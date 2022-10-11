package dev.andante.mccic.qol.mixin.client;

import dev.andante.mccic.qol.client.config.QoLClientConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity> {
    /**
     * Overrides the usual frustum calculations with a frustum expanded by 2.0, when configured.
     */
    @Inject(
        method = "shouldRender",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;getVisibilityBoundingBox()Lnet/minecraft/util/math/Box;",
            shift = At.Shift.BEFORE
        ),
        cancellable = true
    )
    private void onShouldRender(T entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (QoLClientConfig.getConfig().extendedFrustums()) {
            if (entity instanceof ArmorStandEntity || entity instanceof AbstractDecorationEntity) {
                Box box = entity.getVisibilityBoundingBox().expand(2.0); // 0.5 -> 2.0
                if (box.isValid() || box.getAverageSideLength() == 0.0) {
                    box = new Box(entity.getX() - 2.0, entity.getY() - 2.0, entity.getZ() - 2.0, entity.getX() + 2.0, entity.getY() + 2.0, entity.getZ() + 2.0);
                }
                cir.setReturnValue(frustum.isVisible(box));
            }
        }
    }
}

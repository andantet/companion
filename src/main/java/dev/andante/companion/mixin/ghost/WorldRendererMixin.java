package dev.andante.companion.mixin.ghost;

import dev.andante.companion.api.player.ghost.GhostPlayerManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    @Shadow protected abstract void renderEntity(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers);

    @Shadow @Final private BufferBuilderStorage bufferBuilders;
    @Shadow private @Nullable PostEffectProcessor entityOutlinePostProcessor;
    @Shadow @Final private MinecraftClient client;

    /**
     * Renders ghost players.
     */
    @SuppressWarnings("InvalidInjectorMethodSignature")
    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;drawCurrentLayer()V",
                    ordinal = 0,
                    shift = At.Shift.BEFORE
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void renderGhostPlayers(
            MatrixStack matrices,
            float tickDelta,
            long limitTime,
            boolean renderBlockOutline,
            Camera camera,
            GameRenderer gameRenderer,
            LightmapTextureManager lightmapTextureManager,
            Matrix4f positionMatrix,
            CallbackInfo ci,
            Profiler profiler,
            Vec3d cameraPos,
            double cameraX,
            double cameraY,
            double cameraZ,
            Matrix4f peekedPositionMatrix,
            boolean frustumCaptured,
            Frustum frustum,
            boolean renderOutlines,
            VertexConsumerProvider.Immediate immediate
    ) {
        GhostPlayerManager.INSTANCE.getPlayers().forEach(player -> {
            VertexConsumerProvider renderedImmediate = immediate;

            if (player.isGlowing()) {
                OutlineVertexConsumerProvider outlineVertexConsumerProvider = this.bufferBuilders.getOutlineVertexConsumers();
                int colorValue = player.getTeamColorValue();
                outlineVertexConsumerProvider.setColor(ColorHelper.Argb.getRed(colorValue), ColorHelper.Argb.getGreen(colorValue), ColorHelper.Argb.getBlue(colorValue), 255);
                renderedImmediate = outlineVertexConsumerProvider;
            }

            renderEntity(player, cameraX, cameraY, cameraZ, tickDelta, matrices, renderedImmediate);
        });
    }

    /**
     * Renders ghost players.
     */
    @SuppressWarnings({"InvalidInjectorMethodSignature"})
    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/OutlineVertexConsumerProvider;draw()V",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void renderGhostPlayerOutlines(
            MatrixStack matrices,
            float tickDelta,
            long limitTime,
            boolean renderBlockOutline,
            Camera camera,
            GameRenderer gameRenderer,
            LightmapTextureManager lightmapTextureManager,
            Matrix4f positionMatrix,
            CallbackInfo ci,
            Profiler profiler,
            Vec3d cameraPos,
            double cameraX,
            double cameraY,
            double cameraZ,
            Matrix4f peekedPositionMatrix,
            boolean frustumCaptured,
            Frustum frustum,
            boolean renderOutlines
    ) {
        if (!renderOutlines && GhostPlayerManager.INSTANCE.shouldRenderGlowing()) {
            if (this.entityOutlinePostProcessor != null) {
                this.entityOutlinePostProcessor.render(tickDelta);
                this.client.getFramebuffer().beginWrite(false);
            }
        }
    }
}

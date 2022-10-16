package dev.andante.mccic.qol.mixin.client;

import dev.andante.mccic.api.client.game.GameTracker;
import dev.andante.mccic.api.game.Game;
import dev.andante.mccic.api.game.GameState;
import dev.andante.mccic.qol.client.config.QoLClientConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    @Shadow private boolean renderHitboxes;
    @Shadow private static void renderHitbox(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta) { throw new AssertionError(); }

    /**
     * Force render hitboxes if configured.
     */
    @Inject(
        method = "render",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;renderHitboxes:Z",
            shift = At.Shift.BEFORE
        )
    )
    private <E extends Entity> void onRenderHitbox(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertices, int light, CallbackInfo ci) {
        GameTracker tracker = GameTracker.INSTANCE;
        if (tracker.getGameState() != GameState.ACTIVE) {
            return;
        }

        Game game = tracker.getGame();
        QoLClientConfig config = QoLClientConfig.getConfig();
        if ((game == Game.SKY_BATTLE && config.autoHitboxSkyBattle()) || (game == Game.BATTLE_BOX && config.autoHitboxBattleBox())) {
            if (!this.renderHitboxes && !entity.isInvisible() && !MinecraftClient.getInstance().hasReducedDebugInfo()) {
                renderHitbox(matrices, vertices.getBuffer(RenderLayer.getLines()), entity, tickDelta);
            }
        }
    }
}

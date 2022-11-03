package dev.andante.mccic.hud.mixin.client;

import dev.andante.mccic.api.client.tracker.GameTracker;
import dev.andante.mccic.hud.client.config.HudClientConfig;
import dev.andante.mccic.hud.client.render.MCCIHudRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/MinecraftClient;isDemo()Z",
            shift = At.Shift.BEFORE
        )
    )
    private void onRender(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (GameTracker.INSTANCE.isOnServer()) {
            HudClientConfig config = HudClientConfig.getConfig();
            if (config.enabled()) {
                MCCIHudRenderer.INSTANCE.render(matrices, tickDelta, config);
            }
        }
    }
}

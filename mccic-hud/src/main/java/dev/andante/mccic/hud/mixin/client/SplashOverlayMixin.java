package dev.andante.mccic.hud.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.andante.mccic.api.client.tracker.GameTracker;
import dev.andante.mccic.hud.client.config.HudClientConfig;
import dev.andante.mccic.hud.client.render.MCCICLogoTexture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(SplashOverlay.class)
public class SplashOverlayMixin {
    @Inject(method = "init", at = @At("TAIL"))
    private static void onInit(MinecraftClient client, CallbackInfo ci) {
        client.getTextureManager().registerTexture(MCCICLogoTexture.MCCIC_LOGO, new MCCICLogoTexture());
    }

    @ModifyArg(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIFFIIII)V",
                    ordinal = 0
            ),
            index = 0
    )
    private Identifier modifyLogoTexture(Identifier id) {
        return mccic_useCustomLoadingScreen() ? MCCICLogoTexture.MCCIC_LOGO : id;
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;blendFunc(II)V",
                    shift = At.Shift.BEFORE
            )
    )
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (mccic_useCustomLoadingScreen()) {
            RenderSystem.defaultBlendFunc();
        }
    }

    @Inject(method = "method_35733", at = @At("HEAD"), cancellable = true, remap = false)
    private static void onBrandColor(CallbackInfoReturnable<Integer> cir) {
        if (mccic_useCustomLoadingScreen()) {
            cir.setReturnValue(MCCICLogoTexture.MCCIC_BACKGROUND_COLOR);
        }
    }

    @Unique
    private static boolean mccic_useCustomLoadingScreen() {
        return GameTracker.INSTANCE.isOnServer() && HudClientConfig.getConfig().mccicLoadingScreen();
    }
}

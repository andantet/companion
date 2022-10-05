package dev.andante.mccic.api.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.andante.mccic.api.client.toast.MCCICToast;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(SystemToast.class)
public class SystemToastMixin {
    /**
     * Replaces texture with correct texture.
     */
    @Inject(
        method = "draw",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/util/Identifier;)V",
            shift = At.Shift.AFTER
        )
    )
    private void onDrawSetTexture(MatrixStack matrices, ToastManager manager, long startTime, CallbackInfoReturnable<Toast.Visibility> cir) {
        SystemToast that = (SystemToast) (Object) this;
        if (that instanceof MCCICToast) {
            RenderSystem.setShaderTexture(0, MCCICToast.TEXTURE);
        }
    }
}

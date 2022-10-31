package dev.andante.mccic.qol.mixin.client;

import dev.andante.mccic.api.client.tracker.GameTracker;
import dev.andante.mccic.api.game.GameState;
import dev.andante.mccic.qol.client.config.QoLClientConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(HandledScreen.class)
public class HandledScreenMixin {
    @Shadow @Nullable protected Slot focusedSlot;
    @Unique private static Slot staticFocusedSlot;

    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        staticFocusedSlot = null;
    }

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawSlotHighlight(Lnet/minecraft/client/util/math/MatrixStack;III)V"
        )
    )
    private void onRenderDrawSlotHighlight(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        staticFocusedSlot = this.focusedSlot;
    }

    /**
     * Cancels rendering of slot highlight when on MCCI, not in-game, configured and slot is empty.
     */
    @Inject(method = "drawSlotHighlight", at = @At("HEAD"), cancellable = true)
    private static void onRenderDrawSlotHighlight(MatrixStack matrices, int x, int y, int z, CallbackInfo ci) {
        GameTracker tracker = GameTracker.INSTANCE;
        if (tracker.isOnServer()) {
            GameState state = tracker.getGameState();
            if (state != GameState.ACTIVE && state != GameState.WAITING_FOR_GAME) {
                if (staticFocusedSlot != null) {
                    if (QoLClientConfig.getConfig().emptySlotHighlightsFix() && !staticFocusedSlot.hasStack()) {
                        ci.cancel();
                    }
                }
            }
        }
    }
}

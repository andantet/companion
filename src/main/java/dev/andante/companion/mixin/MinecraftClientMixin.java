package dev.andante.companion.mixin;

import dev.andante.companion.api.event.ScreenClosedCallback;
import dev.andante.companion.api.event.WorldJoinCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow @Nullable public Screen currentScreen;

    /**
     * Hands off the tail of joinWorld to an event.
     */
    @Inject(method = "joinWorld", at = @At("TAIL"))
    private void onJoinWorld(ClientWorld world, CallbackInfo ci) {
        WorldJoinCallback.Companion.getEVENT().invoker().onJoinWorld(world);
    }

    /**
     * Hands off the head of setScreen to an event.
     */
    @Inject(method = "setScreen", at = @At("HEAD"))
    private void onSetScreen(Screen screen, CallbackInfo ci) {
        if (screen == null && this.currentScreen != null) {
            ScreenClosedCallback.Companion.getEVENT().invoker().onCloseScreen(this.currentScreen);
        }
    }
}

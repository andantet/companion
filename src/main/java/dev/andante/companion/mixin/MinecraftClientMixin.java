package dev.andante.companion.mixin;

import dev.andante.companion.api.event.WorldJoinCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    /**
     * Hands off the tail of joinWorld to an event.
     */
    @Inject(method = "joinWorld", at = @At("TAIL"))
    private void onJoinWorld(ClientWorld world, CallbackInfo ci) {
        WorldJoinCallback.Companion.getEVENT().invoker().onJoinWorld(world);
    }
}

package dev.andante.companion.mixin;

import dev.andante.companion.api.event.SoundPlayCallback;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundSystem.class)
public class SoundSystemMixin {
    @Inject(
        method = "play(Lnet/minecraft/client/sound/SoundInstance;)V",
        at = @At(
            value = "INVOKE",
            target = "Lorg/slf4j/Logger;debug(Lorg/slf4j/Marker;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V",
            ordinal = 0,
            remap = false
        )
    )
    private void onPlay(SoundInstance sound, CallbackInfo ci) {
        SoundPlayCallback.Companion.getEVENT().invoker().onSoundPlay(sound);
    }
}

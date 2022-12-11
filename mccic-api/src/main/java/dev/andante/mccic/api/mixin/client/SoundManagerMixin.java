package dev.andante.mccic.api.mixin.client;

import dev.andante.mccic.api.MCCIC;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(SoundManager.class)
public class SoundManagerMixin {
    /**
     * Don't log error message if MCCIC sound event.
     */
    @Inject(
        method = "isSoundResourcePresent",
        at = @At(
            value = "INVOKE",
            target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V",
            shift = At.Shift.BEFORE,
            remap = false
        ),
        cancellable = true
    )
    private static void onIsSoundResourcePresent(Sound sound, Identifier id, ResourceFactory resourceFactory, CallbackInfoReturnable<Boolean> cir) {
        if (id.getNamespace().startsWith(MCCIC.MOD_ID)) {
            cir.setReturnValue(false);
        }
    }
}

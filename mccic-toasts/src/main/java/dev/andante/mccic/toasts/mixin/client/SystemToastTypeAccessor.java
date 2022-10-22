package dev.andante.mccic.toasts.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.toast.SystemToast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(SystemToast.Type.class)
public interface SystemToastTypeAccessor {
    @Accessor long getDisplayDuration();
}

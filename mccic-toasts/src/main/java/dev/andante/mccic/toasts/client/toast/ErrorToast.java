package dev.andante.mccic.toasts.client.toast;

import dev.andante.mccic.api.client.toast.AdaptableIconToast;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public final class ErrorToast extends AdaptableIconToast {

    public static final @NotNull Identifier ERROR_TEXTURE = new Identifier("mccic-toasts", "textures/gui/toasts/error.png");

    public ErrorToast(@NotNull Text... lines) {
        super(ERROR_TEXTURE, Text.of("Error"), lines);
    }

}

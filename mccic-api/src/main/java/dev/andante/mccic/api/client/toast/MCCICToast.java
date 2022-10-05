package dev.andante.mccic.api.client.toast;

import dev.andante.mccic.api.MCCIC;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class MCCICToast extends SystemToast {
    public static final Identifier TEXTURE = new Identifier("%s-api".formatted(MCCIC.MOD_ID), "textures/gui/toasts.png");

    public MCCICToast(Text title, @Nullable Text description) {
        super(Type.PERIODIC_NOTIFICATION, title, description);
    }
}

package dev.andante.mccic.api.client.toast;

import dev.andante.mccic.api.MCCICApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.Toast;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class CustomToast extends SystemToast implements CustomToastTexture {
    public static final Identifier ANNOUNCEMENT_TEXTURE = new Identifier(MCCICApi.MOD_ID, "textures/gui/toasts/announcement.png");

    private final Identifier texture;

    public CustomToast(Text title, @Nullable Text description, Identifier texture) {
        super(Type.PERIODIC_NOTIFICATION, title, description);
        this.texture = texture;
    }

    public CustomToast(Text title, @Nullable Text description) {
        this(title, description, ANNOUNCEMENT_TEXTURE);
    }

    public static void add(Toast toast) {
        MinecraftClient.getInstance().getToastManager().add(toast);
    }

    public void add() {
        add(this);
    }

    @Override
    public Identifier getTexture() {
        return this.texture;
    }
}

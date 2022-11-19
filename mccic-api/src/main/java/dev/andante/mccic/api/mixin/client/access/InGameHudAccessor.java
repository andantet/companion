package dev.andante.mccic.api.mixin.client.access;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public interface InGameHudAccessor {
    @Accessor Text getOverlayMessage();
}

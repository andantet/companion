package dev.andante.mccic.api.client.toast;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public interface CustomToastTexture {
    Identifier getTexture();
}

package dev.andante.mccic.qol.client.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.StringIdentifiable;

@Environment(EnvType.CLIENT)
public enum ConfirmDisconnectMode implements StringIdentifiable {
    OFF,
    IN_GAME,
    ON_SERVER;

    @Override
    public String asString() {
        return this.name();
    }
}

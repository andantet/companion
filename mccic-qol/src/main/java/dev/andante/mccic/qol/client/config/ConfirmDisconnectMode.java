package dev.andante.mccic.qol.client.config;

import dev.andante.mccic.api.util.EnumHelper;
import dev.andante.mccic.config.EnumOption;
import dev.andante.mccic.qol.MCCICQoL;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Locale;

@Environment(EnvType.CLIENT)
public enum ConfirmDisconnectMode implements EnumOption {
    OFF,
    IN_GAME,
    ON_SERVER;

    @Override
    public String getEnumIdentifier() {
        return "confirm_disconnect_mode";
    }

    @Override
    public String getIdentifier() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    @Override
    public int getId() {
        return this.ordinal();
    }

    @Override
    public String getModId() {
        return MCCICQoL.MOD_ID;
    }

    public static ConfirmDisconnectMode byId(int ordinal) {
        return EnumHelper.byId(ConfirmDisconnectMode.class, ordinal);
    }
}

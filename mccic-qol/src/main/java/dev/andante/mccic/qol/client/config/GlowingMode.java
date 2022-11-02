package dev.andante.mccic.qol.client.config;

import dev.andante.mccic.api.util.EnumHelper;
import dev.andante.mccic.config.EnumOption;
import dev.andante.mccic.qol.MCCICQoL;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Locale;

@Environment(EnvType.CLIENT)
public enum GlowingMode implements EnumOption {
    DEFAULT,
    DISABLED,
    DISABLED_FOR_PLAYERS;

    @Override
    public String getEnumIdentifier() {
        return "glowing_mode";
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

    public static GlowingMode byId(int ordinal) {
        return EnumHelper.byId(GlowingMode.class, ordinal);
    }
}

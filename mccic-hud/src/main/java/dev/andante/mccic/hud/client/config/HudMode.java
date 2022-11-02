package dev.andante.mccic.hud.client.config;

import dev.andante.mccic.api.util.EnumHelper;
import dev.andante.mccic.config.EnumOption;
import dev.andante.mccic.hud.MCCICHud;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Locale;

@Environment(EnvType.CLIENT)
public enum HudMode implements EnumOption {
    DEFAULT,
    DISABLED,
    CUSTOM;

    @Override
    public String getEnumIdentifier() {
        return "hud_mode";
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
        return MCCICHud.MOD_ID;
    }

    public static HudMode byId(int ordinal) {
        return EnumHelper.byId(HudMode.class, ordinal);
    }
}

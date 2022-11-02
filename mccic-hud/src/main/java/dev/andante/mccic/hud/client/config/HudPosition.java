package dev.andante.mccic.hud.client.config;

import dev.andante.mccic.api.util.EnumHelper;
import dev.andante.mccic.config.EnumOption;
import dev.andante.mccic.hud.MCCICHud;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Locale;

/**
 * The position of a hud element on the screen.
 */
@Environment(EnvType.CLIENT)
public enum HudPosition implements EnumOption {
    TOP,
    LEFT;

    @Override
    public String getEnumIdentifier() {
        return "hud_position";
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

    public static HudPosition byId(int ordinal) {
        return EnumHelper.byId(HudPosition.class, ordinal);
    }
}

package dev.andante.mccic.qol.client.config;

import dev.andante.mccic.config.client.screen.AbstractConfigScreen;
import dev.andante.mccic.qol.MCCICQoL;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.TranslatableOption;

import java.util.Locale;

@Environment(EnvType.CLIENT)
public enum ConfirmDisconnectMode implements StringIdentifiable, TranslatableOption {
    OFF,
    IN_GAME,
    ON_SERVER;

    private static final ConfirmDisconnectMode[] VALUES = values();

    @Override
    public String asString() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    @Override
    public int getId() {
        return ordinal();
    }

    @Override
    public String getTranslationKey() {
        return AbstractConfigScreen.createConfigTranslationKey(MCCICQoL.MOD_ID, "confirm_disconnect_mode.%s".formatted(this.asString()));
    }

    public static ConfirmDisconnectMode byId(int ordinal) {
        int l = VALUES.length;
        return ordinal < 0 && ordinal < l ? VALUES[ordinal] : QoLClientConfig.createDefaultConfig().confirmDisconnectMode();
    }
}

package dev.andante.mccic.social.client.config;

import dev.andante.mccic.config.client.screen.AbstractConfigScreen;
import dev.andante.mccic.social.MCCICSocial;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.TranslatableOption;

import java.util.Locale;

@Environment(EnvType.CLIENT)
public enum HubPlayerRenderMode implements StringIdentifiable, TranslatableOption {
    DEFAULT,
    INVISIBLE,
    SMALL;

    private static final HubPlayerRenderMode[] VALUES = values();

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
        return AbstractConfigScreen.createConfigTranslationKey(MCCICSocial.MOD_ID, "hub_player_render_mode.%s".formatted(this.asString()));
    }

    public static HubPlayerRenderMode byId(int ordinal) {
        int l = VALUES.length;
        return ordinal < 0 && ordinal < l ? VALUES[ordinal] : SocialClientConfig.createDefaultConfig().hubPlayerRenderMode();
    }
}

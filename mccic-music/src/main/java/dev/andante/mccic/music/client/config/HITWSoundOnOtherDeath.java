package dev.andante.mccic.music.client.config;

import dev.andante.mccic.config.client.screen.AbstractConfigScreen;
import dev.andante.mccic.music.MCCICMusic;
import dev.andante.mccic.music.MCCICSounds;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.TranslatableOption;

import java.util.Locale;

public enum HITWSoundOnOtherDeath implements StringIdentifiable, TranslatableOption {
    OFF,
    TEAM_ELIMINATED(MCCICSounds.TEAM_ELIMINATED),
    EARLY_ELIMINATION(MCCICSounds.EARLY_ELIMINATION),
    SCORE(true, true, MCCICSounds.SCORE_ACQUIRED, MCCICSounds.SCORE_BIG_COINS),
    SCORE_ACQUIRED(true, true, MCCICSounds.SCORE_ACQUIRED),
    SCORE_BIG_COINS(true, true, MCCICSounds.SCORE_BIG_COINS);

    private final Identifier[] sounds;

    private static final HITWSoundOnOtherDeath[] VALUES = values();
    private final boolean score, randomPitch;

    HITWSoundOnOtherDeath(boolean score, boolean randomPitch, Identifier... sounds) {
        this.score = score;
        this.randomPitch = randomPitch;
        this.sounds = sounds;
    }

    HITWSoundOnOtherDeath(Identifier... sounds) {
        this(false, false, sounds);
    }

    public Identifier[] getSounds() {
        return this.sounds;
    }

    public boolean isScore() {
        return this.score;
    }

    public boolean hasRandomPitch() {
        return this.randomPitch;
    }

    @Override
    public String asString() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    @Override
    public int getId() {
        return this.ordinal();
    }

    @Override
    public String getTranslationKey() {
        return AbstractConfigScreen.createConfigTranslationKey(MCCICMusic.MOD_ID, "hitw_sound_on_other_death.%s".formatted(this.asString()));
    }

    public static HITWSoundOnOtherDeath byId(int ordinal) {
        int l = VALUES.length;
        return ordinal < 0 && ordinal < l ? VALUES[ordinal] : MusicClientConfig.createDefaultConfig().hitwSoundOnOtherDeath();
    }
}

package dev.andante.mccic.music.client.config;

import dev.andante.mccic.api.sound.MCCICSounds;
import dev.andante.mccic.api.util.EnumHelper;
import dev.andante.mccic.config.EnumOption;
import dev.andante.mccic.music.MCCICMusic;
import net.minecraft.util.Identifier;

import java.util.Locale;

public enum HITWSoundOnOtherDeath implements EnumOption {
    OFF,
    TEAM_ELIMINATED(MCCICSounds.TEAM_ELIMINATED),
    EARLY_ELIMINATION(MCCICSounds.EARLY_ELIMINATION),
    SCORE(true, false, MCCICSounds.SCORE_ACQUIRED, MCCICSounds.SCORE_BIG_COINS),
    SCORE_ACQUIRED(true, false, MCCICSounds.SCORE_ACQUIRED),
    SCORE_BIG_COINS(true, true, MCCICSounds.SCORE_BIG_COINS);

    private final Identifier[] sounds;
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
    public String getEnumIdentifier() {
        return "hitw_sound_on_other_death";
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
        return MCCICMusic.MOD_ID;
    }

    public static HITWSoundOnOtherDeath byId(int ordinal) {
        return EnumHelper.byId(HITWSoundOnOtherDeath.class, ordinal);
    }
}

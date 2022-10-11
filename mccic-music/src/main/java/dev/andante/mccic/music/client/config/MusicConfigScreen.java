package dev.andante.mccic.music.client.config;

import dev.andante.mccic.config.client.screen.AbstractConfigScreen;
import dev.andante.mccic.music.MCCICMusic;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.SimpleOption;

public class MusicConfigScreen extends AbstractConfigScreen<MusicClientConfig> {
    public static final SimpleOption<Double> MUSIC_VOLUME_OPTION;
    public static final SimpleOption<Double> MUSIC_VOLUME_AFTER_DEATH_OPTION;
    public static final SimpleOption<HITWSoundOnOtherDeath> HITW_SOUND_ON_OTHER_DEATH;

    public MusicConfigScreen(Screen parent) {
        super(MCCICMusic.MOD_ID, parent, MusicClientConfig.CONFIG_HOLDER);
    }

    @Override
    protected void init() {
        super.init();
        this.list.addSingleOptionEntry(MUSIC_VOLUME_OPTION);
        this.list.addSingleOptionEntry(MUSIC_VOLUME_AFTER_DEATH_OPTION);
        this.list.addSingleOptionEntry(HITW_SOUND_ON_OTHER_DEATH);
    }

    @Override
    protected void saveConfig() {
        MusicClientConfig.CONFIG_HOLDER.set(new MusicClientConfig(MUSIC_VOLUME_OPTION.getValue(), MUSIC_VOLUME_AFTER_DEATH_OPTION.getValue(), HITW_SOUND_ON_OTHER_DEATH.getValue()));
        super.saveConfig();
    }

    static {
        MusicClientConfig config = MusicClientConfig.getConfig();
        MusicClientConfig defaultConfig = MusicClientConfig.createDefaultConfig();
        MUSIC_VOLUME_OPTION = ofDouble(MCCICMusic.MOD_ID, "music_volume", config.musicVolume(), defaultConfig.musicVolume());
        MUSIC_VOLUME_AFTER_DEATH_OPTION = ofDouble(MCCICMusic.MOD_ID, "music_volume_after_death", config.musicVolumeAfterDeath(), defaultConfig.musicVolumeAfterDeath());
        HITW_SOUND_ON_OTHER_DEATH = ofEnum(MCCICMusic.MOD_ID, "hitw_sound_on_other_death", HITWSoundOnOtherDeath::byId, HITWSoundOnOtherDeath.values(), config.hitwSoundOnOtherDeath(), defaultConfig.hitwSoundOnOtherDeath());
    }
}

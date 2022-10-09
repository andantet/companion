package dev.andante.mccic.music.client.config;

import dev.andante.mccic.config.client.screen.MCCICAbstractConfigScreen;
import dev.andante.mccic.music.MCCICMusic;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.SimpleOption;

public class MCCICMusicConfigScreen extends MCCICAbstractConfigScreen<MusicClientConfig> {
    public static final SimpleOption<Double> MUSIC_VOLUME_OPTION;
    public static final SimpleOption<Double> MUSIC_VOLUME_AFTER_DEATH_OPTION;

    public MCCICMusicConfigScreen(Screen parent) {
        super(MCCICMusic.MOD_ID, parent, MusicClientConfig.CONFIG_HOLDER);
    }

    @Override
    protected void init() {
        super.init();
        this.list.addSingleOptionEntry(MUSIC_VOLUME_OPTION);
        this.list.addSingleOptionEntry(MUSIC_VOLUME_AFTER_DEATH_OPTION);
    }

    @Override
    protected void saveConfig() {
        MusicClientConfig.CONFIG_HOLDER.set(new MusicClientConfig(MUSIC_VOLUME_OPTION.getValue(), MUSIC_VOLUME_AFTER_DEATH_OPTION.getValue()));
        super.saveConfig();
    }

    static {
        MusicClientConfig defaultConfig = MusicClientConfig.createDefaultConfig();
        MUSIC_VOLUME_OPTION = ofDouble(MCCICMusic.MOD_ID, "music_volume", defaultConfig.musicVolume());
        MUSIC_VOLUME_AFTER_DEATH_OPTION = ofDouble(MCCICMusic.MOD_ID, "music_volume_after_death", defaultConfig.musicVolumeAfterDeath());
    }
}

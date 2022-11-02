package dev.andante.mccic.music.client.config;

import dev.andante.mccic.config.client.screen.AbstractConfigScreen;
import dev.andante.mccic.music.MCCICMusic;
import dev.andante.mccic.music.client.MCCICMusicClientImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;

import java.util.List;

@Environment(EnvType.CLIENT)
public class MusicConfigScreen extends AbstractConfigScreen<MusicClientConfig> {
    public static final SimpleOption<Float> MUSIC_VOLUME_OPTION;
    public static final SimpleOption<Float> MUSIC_VOLUME_AFTER_DEATH_OPTION;
    public static final SimpleOption<HITWSoundOnOtherDeath> HITW_SOUND_ON_OTHER_DEATH;

    public MusicConfigScreen(Screen parent) {
        super(MCCICMusic.MOD_ID, parent, MusicClientConfig.CONFIG_HOLDER);
    }

    @Override
    protected List<SimpleOption<?>> getOptions() {
        return List.of(MUSIC_VOLUME_OPTION, MUSIC_VOLUME_AFTER_DEATH_OPTION, HITW_SOUND_ON_OTHER_DEATH);
    }

    @Override
    public MusicClientConfig createConfig() {
        return new MusicClientConfig(MUSIC_VOLUME_OPTION.getValue(), MUSIC_VOLUME_AFTER_DEATH_OPTION.getValue(), HITW_SOUND_ON_OTHER_DEATH.getValue());
    }

    static {
        MusicClientConfig config = MusicClientConfig.getConfig();
        MusicClientConfig defaultConfig = MusicClientConfig.createDefaultConfig();
        Random random = Random.create();
        MUSIC_VOLUME_OPTION = ofFloat(MCCICMusic.MOD_ID, "game_music_volume", config, defaultConfig, MusicClientConfig::gameMusicVolume);
        MUSIC_VOLUME_AFTER_DEATH_OPTION = ofFloat(MCCICMusic.MOD_ID, "game_music_volume_after_death", config, defaultConfig, MusicClientConfig::gameMusicVolumeAfterDeath);
        HITW_SOUND_ON_OTHER_DEATH = ofEnum(MCCICMusic.MOD_ID, "hitw_sound_on_other_death", HITWSoundOnOtherDeath::byId, HITWSoundOnOtherDeath.values(), config, defaultConfig, MusicClientConfig::hitwSoundOnOtherDeath,
            SimpleOption.constantTooltip(Text.translatable(AbstractConfigScreen.createConfigTranslationKey(MCCICMusic.MOD_ID, "hitw_sound_on_other_death.tooltip"))),
            value -> {
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.currentScreen != null) {
                    MCCICMusicClientImpl.playHoleInTheWallOtherDeathSound(config, value, client, random, false);
                }
            }
        );
    }
}

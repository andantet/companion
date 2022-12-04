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
    public final SimpleOption<Float> musicVolumeOption;
    public final SimpleOption<Float> musicVolumeAfterDeathOption;
    public final SimpleOption<Float> sfxVolumeOption;
    public final SimpleOption<HITWSoundOnOtherDeath> hitwSoundOnOtherDeathOption;
    public final SimpleOption<Boolean> stopMusicOnDeathOption;
    public final SimpleOption<Boolean> stopMusicOnChickenHitOption;

    public MusicConfigScreen(Screen parent) {
        super(MCCICMusic.MOD_ID, parent, MusicClientConfig.CONFIG_HOLDER);

        Random random = Random.create();
        this.musicVolumeOption = this.ofFloat("game_music_volume", MusicClientConfig::gameMusicVolume);
        this.musicVolumeAfterDeathOption = this.ofFloat("game_music_volume_after_death", MusicClientConfig::gameMusicVolumeAfterDeath);
        this.sfxVolumeOption = this.ofFloat("sfx_volume", MusicClientConfig::sfxVolume);
        this.hitwSoundOnOtherDeathOption = this.ofEnum("hitw_sound_on_other_death", HITWSoundOnOtherDeath::byId, HITWSoundOnOtherDeath.values(), MusicClientConfig::hitwSoundOnOtherDeath,
            SimpleOption.constantTooltip(Text.translatable(this.createConfigTranslationKey("hitw_sound_on_other_death.tooltip"))),
            value -> {
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.currentScreen instanceof MusicConfigScreen) {
                    MCCICMusicClientImpl.playHoleInTheWallOtherDeathSound(this.getConfig(), value, client, random);
                }
            }
        );
        this.stopMusicOnDeathOption = this.ofBooleanTooltip("stop_music_on_death", MusicClientConfig::stopMusicOnDeath);
        this.stopMusicOnChickenHitOption = this.ofBooleanTooltip("stop_music_on_chicken_hit", MusicClientConfig::stopMusicOnChickenHit);
    }

    @Override
    protected List<SimpleOption<?>> getOptions() {
        return List.of(this.musicVolumeOption, this.musicVolumeAfterDeathOption, this.sfxVolumeOption, this.hitwSoundOnOtherDeathOption, this.stopMusicOnDeathOption, this.stopMusicOnChickenHitOption);
    }

    @Override
    public MusicClientConfig createConfig() {
        return new MusicClientConfig(this.musicVolumeOption.getValue(), this.musicVolumeAfterDeathOption.getValue(), this.sfxVolumeOption.getValue(), this.hitwSoundOnOtherDeathOption.getValue(), this.stopMusicOnDeathOption.getValue(), this.stopMusicOnChickenHitOption.getValue());
    }

    @Override
    public MusicClientConfig getConfig() {
        return MusicClientConfig.getConfig();
    }

    @Override
    public MusicClientConfig getDefaultConfig() {
        return MusicClientConfig.createDefaultConfig();
    }
}

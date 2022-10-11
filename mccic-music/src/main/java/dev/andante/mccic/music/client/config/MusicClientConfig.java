package dev.andante.mccic.music.client.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.andante.mccic.config.ConfigHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.StringIdentifiable;

@Environment(EnvType.CLIENT)
public record MusicClientConfig(float musicVolume, float musicVolumeAfterDeath, HITWSoundOnOtherDeath hitwSoundOnOtherDeath) {
    public static final Codec<MusicClientConfig> CODEC = RecordCodecBuilder.create(
        instance -> {
            MusicClientConfig defaultConfig = createDefaultConfig();
            return instance.group(
                Codec.FLOAT.fieldOf("music_volume")
                           .orElse(defaultConfig.musicVolume())
                           .forGetter(MusicClientConfig::musicVolume),
                Codec.FLOAT.fieldOf("music_volume_after_death")
                           .orElse(defaultConfig.musicVolumeAfterDeath())
                           .forGetter(MusicClientConfig::musicVolumeAfterDeath),
                StringIdentifiable.createCodec(HITWSoundOnOtherDeath::values)
                                  .fieldOf("hitw_sound_on_other_death")
                                  .orElse(defaultConfig.hitwSoundOnOtherDeath())
                                  .forGetter(MusicClientConfig::hitwSoundOnOtherDeath)
            ).apply(instance, MusicClientConfig::new);
        }
    );

    public static final ConfigHolder<MusicClientConfig> CONFIG_HOLDER = new ConfigHolder<>("music", CODEC, createDefaultConfig());

    public MusicClientConfig(double musicVolume, double musicVolumeAfterDeath, HITWSoundOnOtherDeath hitwSoundOnOtherDeath) {
        this((float) musicVolume, (float) musicVolumeAfterDeath, hitwSoundOnOtherDeath);
    }

    public static MusicClientConfig getConfig() {
        return CONFIG_HOLDER.get();
    }

    public static MusicClientConfig createDefaultConfig() {
        return new MusicClientConfig(1.0F, 0.3F, HITWSoundOnOtherDeath.OFF);
    }
}

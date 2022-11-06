package dev.andante.mccic.music.client.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.andante.mccic.config.ConfigHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.StringIdentifiable;

@Environment(EnvType.CLIENT)
public record MusicClientConfig(float gameMusicVolume, float gameMusicVolumeAfterDeath, float sfxVolume, HITWSoundOnOtherDeath hitwSoundOnOtherDeath) {
    public static final Codec<MusicClientConfig> CODEC = RecordCodecBuilder.create(
        instance -> {
            MusicClientConfig defaultConfig = createDefaultConfig();
            return instance.group(
                Codec.FLOAT.fieldOf("game_music_volume")
                           .orElse(defaultConfig.gameMusicVolume())
                           .forGetter(MusicClientConfig::gameMusicVolume),
                Codec.FLOAT.fieldOf("game_music_volume_after_death")
                           .orElse(defaultConfig.gameMusicVolumeAfterDeath())
                           .forGetter(MusicClientConfig::gameMusicVolumeAfterDeath),
                Codec.FLOAT.fieldOf("sfx_volume")
                           .orElse(defaultConfig.sfxVolume())
                           .forGetter(MusicClientConfig::sfxVolume),
                StringIdentifiable.createCodec(HITWSoundOnOtherDeath::values)
                                  .fieldOf("hitw_sound_on_other_death")
                                  .orElse(defaultConfig.hitwSoundOnOtherDeath())
                                  .forGetter(MusicClientConfig::hitwSoundOnOtherDeath)
            ).apply(instance, MusicClientConfig::new);
        }
    );

    public static final ConfigHolder<MusicClientConfig> CONFIG_HOLDER = new ConfigHolder<>("music", CODEC, createDefaultConfig());

    public static MusicClientConfig getConfig() {
        return CONFIG_HOLDER.get();
    }

    public static MusicClientConfig createDefaultConfig() {
        return new MusicClientConfig(0.5F, 0.25F, 1.0F, HITWSoundOnOtherDeath.OFF);
    }
}

package dev.andante.mccic.music.client.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.andante.mccic.config.ConfigHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public record MusicClientConfig(float musicVolume, float musicVolumeAfterDeath) {
    public static final Codec<MusicClientConfig> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.FLOAT.fieldOf("music_volume").forGetter(MusicClientConfig::musicVolume),
            Codec.FLOAT.fieldOf("music_volume_after_death").forGetter(MusicClientConfig::musicVolumeAfterDeath)
        ).apply(instance, MusicClientConfig::new)
    );

    public static final ConfigHolder<MusicClientConfig> CONFIG_HOLDER = new ConfigHolder<>("music", CODEC, createDefaultConfig());

    public static MusicClientConfig getConfig() {
        return CONFIG_HOLDER.get();
    }

    public static MusicClientConfig createDefaultConfig() {
        return new MusicClientConfig(1.0F, 0.3F);
    }
}

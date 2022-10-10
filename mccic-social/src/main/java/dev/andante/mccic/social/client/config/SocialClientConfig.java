package dev.andante.mccic.social.client.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.andante.mccic.config.ConfigHolder;
import dev.andante.mccic.social.MCCICSocial;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public record SocialClientConfig(boolean allToasts) {
    public static final Codec<SocialClientConfig> CODEC = RecordCodecBuilder.create(
        instance -> {
            SocialClientConfig defaultConfig = SocialClientConfig.createDefaultConfig();
            return instance.group(
                Codec.BOOL.fieldOf("all_toasts")
                          .orElse(defaultConfig.allToasts())
                          .forGetter(SocialClientConfig::allToasts)
            ).apply(instance, SocialClientConfig::new);
        }
    );

    public static final ConfigHolder<SocialClientConfig> CONFIG_HOLDER = new ConfigHolder<>(MCCICSocial.ID, CODEC, createDefaultConfig());

    public static SocialClientConfig getConfig() {
        return CONFIG_HOLDER.get();
    }

    public static SocialClientConfig createDefaultConfig() {
        return new SocialClientConfig(true);
    }
}

package dev.andante.mccic.toasts.client.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.andante.mccic.config.ConfigHolder;
import dev.andante.mccic.toasts.MCCICToasts;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public record ToastsClientConfig(boolean quests, boolean achievements, boolean eventAnnouncements) {
    public static final Codec<ToastsClientConfig> CODEC = RecordCodecBuilder.create(
        instance -> {
            ToastsClientConfig defaultConfig = ToastsClientConfig.createDefaultConfig();
            return instance.group(
                Codec.BOOL.fieldOf("quests")
                          .orElse(defaultConfig.quests())
                          .forGetter(ToastsClientConfig::quests),
                Codec.BOOL.fieldOf("achievements")
                          .orElse(defaultConfig.achievements())
                          .forGetter(ToastsClientConfig::achievements),
                Codec.BOOL.fieldOf("event_announcements")
                          .orElse(defaultConfig.eventAnnouncements())
                          .forGetter(ToastsClientConfig::eventAnnouncements)
            ).apply(instance, ToastsClientConfig::new);
        }
    );

    public static final ConfigHolder<ToastsClientConfig> CONFIG_HOLDER = new ConfigHolder<>(MCCICToasts.ID, CODEC, createDefaultConfig());

    public static ToastsClientConfig getConfig() {
        return CONFIG_HOLDER.get();
    }

    public static ToastsClientConfig createDefaultConfig() {
        return new ToastsClientConfig(true, true, true);
    }
}

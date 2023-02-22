package dev.andante.mccic.toasts.client.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.andante.mccic.config.ConfigCodecBuilder;
import dev.andante.mccic.config.ConfigHolder;
import dev.andante.mccic.toasts.MCCICToasts;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public record ToastsClientConfig(boolean friends, boolean parties, boolean quests, boolean badges, boolean eventAnnouncements, boolean updateNotifications) {
    public static final Codec<ToastsClientConfig> CODEC = RecordCodecBuilder.create(
            instance -> {
                ConfigCodecBuilder<ToastsClientConfig> builder = new ConfigCodecBuilder<>(ToastsClientConfig.createDefaultConfig());
                return instance.group(
                        builder.createBool("friends", ToastsClientConfig::friends),
                        builder.createBool("parties", ToastsClientConfig::parties),
                        builder.createBool("quests", ToastsClientConfig::quests),
                        builder.createBool("badges", ToastsClientConfig::badges),
                        builder.createBool("event_announcements", ToastsClientConfig::eventAnnouncements),
                        builder.createBool("update_notifications", ToastsClientConfig::updateNotifications)
                ).apply(instance, ToastsClientConfig::new);
            }
    );

    public static final ConfigHolder<ToastsClientConfig> CONFIG_HOLDER = new ConfigHolder<>(MCCICToasts.ID, CODEC, createDefaultConfig());

    public static ToastsClientConfig getConfig() {
        return CONFIG_HOLDER.get();
    }

    public static ToastsClientConfig createDefaultConfig() {
        return new ToastsClientConfig(true, true, true, true, true, true);
    }
}

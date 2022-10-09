package dev.andante.mccic.discordrp.client.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.andante.mccic.config.ConfigHolder;
import dev.andante.mccic.discordrp.MCCICDiscordRP;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public record DiscordRPClientConfig(boolean enabled, long clientId) {
    public static final Codec<DiscordRPClientConfig> CODEC = RecordCodecBuilder.create(
        instance -> {
            DiscordRPClientConfig defaultConfig = createDefaultConfig();
            return instance.group(
                Codec.BOOL.fieldOf("enabled")
                          .orElse(defaultConfig.enabled())
                          .forGetter(DiscordRPClientConfig::enabled),
                Codec.LONG.fieldOf("client_id")
                          .orElse(defaultConfig.clientId())
                          .forGetter(DiscordRPClientConfig::clientId)
            ).apply(instance, DiscordRPClientConfig::new);
        }
    );

    public static final ConfigHolder<DiscordRPClientConfig> CONFIG_HOLDER = new ConfigHolder<>(MCCICDiscordRP.ID, CODEC, createDefaultConfig());

    public static DiscordRPClientConfig getConfig() {
        return CONFIG_HOLDER.get();
    }

    public static DiscordRPClientConfig createDefaultConfig() {
        return new DiscordRPClientConfig(true, 1026937264309284935L);
    }
}

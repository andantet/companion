package dev.andante.mccic.discordrp.client.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.andante.mccic.config.ConfigCodecBuilder;
import dev.andante.mccic.config.ConfigHolder;
import dev.andante.mccic.discordrp.MCCICDiscordRP;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public record DiscordRPClientConfig(long clientId, boolean enabled, boolean displayGame, boolean displayGameTime, boolean displayGameState, boolean displayQueue) {
    public static final Codec<DiscordRPClientConfig> CODEC = RecordCodecBuilder.create(
            instance -> {
                ConfigCodecBuilder<DiscordRPClientConfig> builder = new ConfigCodecBuilder<>(DiscordRPClientConfig.createDefaultConfig());
                return instance.group(
                        builder.createLong("client_id", DiscordRPClientConfig::clientId),
                        builder.createBool("enabled", DiscordRPClientConfig::enabled),
                        builder.createBool("display_game", DiscordRPClientConfig::displayGame),
                        builder.createBool("display_game_time", DiscordRPClientConfig::displayGameTime),
                        builder.createBool("display_game_state", DiscordRPClientConfig::displayGameState),
                        builder.createBool("display_queue", DiscordRPClientConfig::displayQueue)
                ).apply(instance, DiscordRPClientConfig::new);
            }
    );

    public static final ConfigHolder<DiscordRPClientConfig> CONFIG_HOLDER = new ConfigHolder<>(MCCICDiscordRP.ID, CODEC, createDefaultConfig());

    public static DiscordRPClientConfig getConfig() {
        return CONFIG_HOLDER.get();
    }

    public static DiscordRPClientConfig createDefaultConfig() {
        return new DiscordRPClientConfig(1026937264309284935L, true, true, true, true, true);
    }
}

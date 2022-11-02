package dev.andante.mccic.discordrp.client.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.andante.mccic.config.ConfigHolder;
import dev.andante.mccic.discordrp.MCCICDiscordRP;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public record DiscordRPClientConfig(long clientId, boolean enabled, boolean displayGame, boolean displayGameTime, boolean displayGameState, boolean displayQueue) {
    public static final Codec<DiscordRPClientConfig> CODEC = RecordCodecBuilder.create(
        instance -> {
            DiscordRPClientConfig defaultConfig = createDefaultConfig();
            return instance.group(

                Codec.LONG.fieldOf("client_id")
                          .orElse(defaultConfig.clientId())
                          .forGetter(DiscordRPClientConfig::clientId),
                Codec.BOOL.fieldOf("enabled")
                          .orElse(defaultConfig.enabled())
                          .forGetter(DiscordRPClientConfig::enabled),
                Codec.BOOL.fieldOf("display_game")
                          .orElse(defaultConfig.displayGame())
                          .forGetter(DiscordRPClientConfig::displayGame),
                Codec.BOOL.fieldOf("display_game_time")
                          .orElse(defaultConfig.displayGameTime())
                          .forGetter(DiscordRPClientConfig::displayGameTime),
                Codec.BOOL.fieldOf("display_game_state")
                          .orElse(defaultConfig.displayGameState())
                          .forGetter(DiscordRPClientConfig::displayGameState),
                Codec.BOOL.fieldOf("display_queue")
                          .orElse(defaultConfig.displayQueue())
                          .forGetter(DiscordRPClientConfig::displayQueue)
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

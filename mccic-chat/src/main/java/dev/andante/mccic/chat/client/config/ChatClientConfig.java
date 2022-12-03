package dev.andante.mccic.chat.client.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.andante.mccic.chat.MCCICChat;
import dev.andante.mccic.config.ConfigCodecBuilder;
import dev.andante.mccic.config.ConfigHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public record ChatClientConfig(boolean mentions, int mentionsColor, boolean hideHitwDeathMessages, boolean hideTgttosDeathMessages) {
    public static final Codec<ChatClientConfig> CODEC = RecordCodecBuilder.create(
            instance -> {
                ConfigCodecBuilder<ChatClientConfig> builder = new ConfigCodecBuilder<>(ChatClientConfig.createDefaultConfig());
                return instance.group(
                        builder.createBool("mentions", ChatClientConfig::mentions),
                        builder.createInt("mentions_color", ChatClientConfig::mentionsColor),
                        builder.createBool("hide_hitw_death_messages", ChatClientConfig::hideHitwDeathMessages),
                        builder.createBool("hide_tgttos_death_messages", ChatClientConfig::hideTgttosDeathMessages)
                ).apply(instance, ChatClientConfig::new);
            }
    );

    public static final ConfigHolder<ChatClientConfig> CONFIG_HOLDER = new ConfigHolder<>(MCCICChat.ID, CODEC, createDefaultConfig());

    public static ChatClientConfig getConfig() {
        return CONFIG_HOLDER.get();
    }

    public static ChatClientConfig createDefaultConfig() {
        return new ChatClientConfig(false, 0xE7FF54, false, false);
    }
}

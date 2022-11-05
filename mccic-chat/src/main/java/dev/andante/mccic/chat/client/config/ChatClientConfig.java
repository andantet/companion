package dev.andante.mccic.chat.client.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.andante.mccic.chat.MCCICChat;
import dev.andante.mccic.config.ConfigHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public record ChatClientConfig(boolean mentions) {
    public static final Codec<ChatClientConfig> CODEC = RecordCodecBuilder.create(
        instance -> {
            ChatClientConfig defaultConfig = createDefaultConfig();
            return instance.group(
                Codec.BOOL.fieldOf("mentions")
                          .orElse(defaultConfig.mentions())
                          .forGetter(ChatClientConfig::mentions)
            ).apply(instance, ChatClientConfig::new);
        }
    );

    public static final ConfigHolder<ChatClientConfig> CONFIG_HOLDER = new ConfigHolder<>(MCCICChat.ID, CODEC, createDefaultConfig());

    public static ChatClientConfig getConfig() {
        return CONFIG_HOLDER.get();
    }

    public static ChatClientConfig createDefaultConfig() {
        return new ChatClientConfig(false);
    }
}

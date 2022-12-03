package dev.andante.mccic.debug.client.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.andante.mccic.config.ConfigCodecBuilder;
import dev.andante.mccic.config.ConfigHolder;
import dev.andante.mccic.debug.MCCICDebug;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public record DebugClientConfig(boolean debugHud, boolean chatAllSounds, boolean rawChat, boolean displayNameSuffix, boolean unpinServerResourcePacks) {
    public static final Codec<DebugClientConfig> CODEC = RecordCodecBuilder.create(
            instance -> {
                ConfigCodecBuilder<DebugClientConfig> builder = new ConfigCodecBuilder<>(DebugClientConfig.createDefaultConfig());
                return instance.group(
                        builder.createBool("debug_hud", DebugClientConfig::debugHud),
                        builder.createBool("chat_all_sounds", DebugClientConfig::chatAllSounds),
                        builder.createBool("raw_chat", DebugClientConfig::rawChat),
                        builder.createBool("display_name_suffix", DebugClientConfig::displayNameSuffix),
                        builder.createBool("unpin_server_resource_packs", DebugClientConfig::unpinServerResourcePacks)
                ).apply(instance, DebugClientConfig::new);
            }
    );

    public static final ConfigHolder<DebugClientConfig> CONFIG_HOLDER = new ConfigHolder<>(MCCICDebug.ID, CODEC, createDefaultConfig());

    public static DebugClientConfig getConfig() {
        return CONFIG_HOLDER.get();
    }

    public static DebugClientConfig createDefaultConfig() {
        return new DebugClientConfig(false, false, false, false, false);
    }
}

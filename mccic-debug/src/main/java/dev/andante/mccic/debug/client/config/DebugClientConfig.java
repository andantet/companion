package dev.andante.mccic.debug.client.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.andante.mccic.config.ConfigHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public record DebugClientConfig(boolean debugHud) {
    public static final Codec<DebugClientConfig> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.BOOL.fieldOf("debug_hud").forGetter(DebugClientConfig::debugHud)
        ).apply(instance, DebugClientConfig::new)
    );

    public static final ConfigHolder<DebugClientConfig> CONFIG_HOLDER = new ConfigHolder<>("debug", CODEC, createDefaultConfig());

    public static DebugClientConfig getConfig() {
        return CONFIG_HOLDER.get();
    }

    public static DebugClientConfig createDefaultConfig() {
        return new DebugClientConfig(false);
    }
}

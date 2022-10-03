package dev.andante.mccic.debug.client.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public record DebugClientConfig(boolean debugHud) {
    public static final Codec<DebugClientConfig> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.BOOL.fieldOf("debug_hud").forGetter(DebugClientConfig::debugHud)
        ).apply(instance, DebugClientConfig::new)
    );

    public static DebugClientConfig createDefaultConfig() {
        return new DebugClientConfig(false);
    }
}

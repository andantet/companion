package dev.andante.mccic.hud.client.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.andante.mccic.config.ConfigHolder;
import dev.andante.mccic.hud.MCCICHud;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.StringIdentifiable;

@Environment(EnvType.CLIENT)
public record HudClientConfig(HudMode topHud, HudPosition timerPosition) {
    public static final Codec<HudClientConfig> CODEC = RecordCodecBuilder.create(
        instance -> {
            HudClientConfig defaultConfig = createDefaultConfig();
            return instance.group(
                StringIdentifiable.createCodec(HudMode::values)
                                  .fieldOf("top_hud")
                                  .orElse(defaultConfig.topHud())
                                  .forGetter(HudClientConfig::topHud),
                StringIdentifiable.createCodec(HudPosition::values)
                                  .fieldOf("timer_position")
                                  .orElse(defaultConfig.timerPosition())
                                  .forGetter(HudClientConfig::timerPosition)
            ).apply(instance, HudClientConfig::new);
        }
    );

    public static final ConfigHolder<HudClientConfig> CONFIG_HOLDER = new ConfigHolder<>(MCCICHud.ID, CODEC, createDefaultConfig());

    public static HudClientConfig getConfig() {
        return CONFIG_HOLDER.get();
    }

    public static HudClientConfig createDefaultConfig() {
        return new HudClientConfig(HudMode.DEFAULT, HudPosition.TOP);
    }
}

package dev.andante.mccic.hud.client.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.andante.mccic.config.ConfigHolder;
import dev.andante.mccic.hud.MCCICHud;
import dev.andante.mccic.hud.client.render.MCCIHudRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.StringIdentifiable;

@Environment(EnvType.CLIENT)
public record HudClientConfig(boolean enabled, HudPosition timerPosition, HudPosition queuePosition) {
    public static final Codec<HudClientConfig> CODEC = RecordCodecBuilder.create(
        instance -> {
            HudClientConfig defaultConfig = createDefaultConfig();
            return instance.group(
                Codec.BOOL.fieldOf("enabled")
                          .orElse(defaultConfig.enabled())
                          .forGetter(HudClientConfig::enabled),
                StringIdentifiable.createCodec(HudPosition::values)
                                  .fieldOf("timer_position")
                                  .orElse(defaultConfig.timerPosition())
                                  .forGetter(HudClientConfig::timerPosition),
                StringIdentifiable.createCodec(HudPosition::values)
                                  .fieldOf("queue_position")
                                  .orElse(defaultConfig.queuePosition())
                                  .forGetter(HudClientConfig::queuePosition)
            ).apply(instance, HudClientConfig::new);
        }
    );

    public static final ConfigHolder<HudClientConfig> CONFIG_HOLDER = new ConfigHolder<>(MCCICHud.ID, CODEC, createDefaultConfig()).registerSaveListener(MCCIHudRenderer.INSTANCE::refreshElementLists);

    public static HudClientConfig getConfig() {
        return CONFIG_HOLDER.get();
    }

    public static HudClientConfig createDefaultConfig() {
        return new HudClientConfig(false, HudPosition.TOP, HudPosition.TOP);
    }
}

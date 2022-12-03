package dev.andante.mccic.hud.client.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.andante.mccic.config.ConfigCodecBuilder;
import dev.andante.mccic.config.ConfigHolder;
import dev.andante.mccic.hud.MCCICHud;
import dev.andante.mccic.hud.client.render.MCCIHudRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public record HudClientConfig(boolean enabled, HudPosition timerPosition, HudPosition queuePosition) {
    public static final Codec<HudClientConfig> CODEC = RecordCodecBuilder.create(
            instance -> {
                ConfigCodecBuilder<HudClientConfig> builder = new ConfigCodecBuilder<>(HudClientConfig.createDefaultConfig());
                return instance.group(
                        builder.createBool("enabled", HudClientConfig::enabled),
                        builder.createEnum("timer_position", HudPosition::values, HudClientConfig::timerPosition),
                        builder.createEnum("queue_position", HudPosition::values, HudClientConfig::queuePosition)
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

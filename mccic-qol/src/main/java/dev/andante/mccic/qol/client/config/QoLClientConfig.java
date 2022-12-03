package dev.andante.mccic.qol.client.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.andante.mccic.config.ConfigCodecBuilder;
import dev.andante.mccic.config.ConfigHolder;
import dev.andante.mccic.qol.MCCICQoL;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public record QoLClientConfig(ConfirmDisconnectMode confirmDisconnectMode, GlowingMode glowingMode, boolean emptySlotHighlightsFix, boolean extendedFrustums, boolean autoHitboxSkyBattle, boolean autoHitboxBattleBox) {
    public static final Codec<QoLClientConfig> CODEC = RecordCodecBuilder.create(
            instance -> {
                ConfigCodecBuilder<QoLClientConfig> builder = new ConfigCodecBuilder<>(QoLClientConfig.createDefaultConfig());
                return instance.group(
                        builder.createEnum("confirm_disconnect_mode", ConfirmDisconnectMode::values, QoLClientConfig::confirmDisconnectMode),
                        builder.createEnum("glowing_mode", GlowingMode::values, QoLClientConfig::glowingMode),
                        builder.createBool("empty_slot_highlights_fix", QoLClientConfig::emptySlotHighlightsFix),
                        builder.createBool("extended_frustums", QoLClientConfig::extendedFrustums),
                        builder.createBool("auto_hitbox_sky_battle", QoLClientConfig::autoHitboxSkyBattle),
                        builder.createBool("auto_hitbox_battle_box", QoLClientConfig::autoHitboxBattleBox)
                ).apply(instance, QoLClientConfig::new);
            }
    );

    public static final ConfigHolder<QoLClientConfig> CONFIG_HOLDER = new ConfigHolder<>(MCCICQoL.ID, CODEC, createDefaultConfig());

    public static QoLClientConfig getConfig() {
        return CONFIG_HOLDER.get();
    }

    public static QoLClientConfig createDefaultConfig() {
        return new QoLClientConfig(ConfirmDisconnectMode.IN_GAME, GlowingMode.DEFAULT, true, true, false, false);
    }
}

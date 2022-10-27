package dev.andante.mccic.qol.client.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.andante.mccic.config.ConfigHolder;
import dev.andante.mccic.qol.MCCICQoL;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.StringIdentifiable;

@Environment(EnvType.CLIENT)
public record QoLClientConfig(ConfirmDisconnectMode confirmDisconnectMode, GlowingMode glowingMode, boolean emptySlotHighlightsFix, boolean extendedFrustums, boolean autoHitboxSkyBattle, boolean autoHitboxBattleBox) {
    public static final Codec<QoLClientConfig> CODEC = RecordCodecBuilder.create(
        instance -> {
            QoLClientConfig defaultConfig = createDefaultConfig();
            return instance.group(
                StringIdentifiable.createCodec(ConfirmDisconnectMode::values)
                                  .fieldOf("confirm_disconnect_mode")
                                  .orElse(defaultConfig.confirmDisconnectMode())
                                  .forGetter(QoLClientConfig::confirmDisconnectMode),
                StringIdentifiable.createCodec(GlowingMode::values)
                                  .fieldOf("glowing_mode")
                                  .orElse(defaultConfig.glowingMode())
                                  .forGetter(QoLClientConfig::glowingMode),
                Codec.BOOL.fieldOf("empty_slot_highlights_fix")
                          .orElse(defaultConfig.emptySlotHighlightsFix())
                          .forGetter(QoLClientConfig::emptySlotHighlightsFix),
                Codec.BOOL.fieldOf("extended_frustums")
                          .orElse(defaultConfig.extendedFrustums())
                          .forGetter(QoLClientConfig::extendedFrustums),
                Codec.BOOL.fieldOf("auto_hitbox_sky_battle")
                          .orElse(defaultConfig.autoHitboxSkyBattle())
                          .forGetter(QoLClientConfig::autoHitboxSkyBattle),
                Codec.BOOL.fieldOf("auto_hitbox_battle_box")
                          .orElse(defaultConfig.autoHitboxBattleBox())
                          .forGetter(QoLClientConfig::autoHitboxBattleBox)
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

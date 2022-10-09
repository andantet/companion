package dev.andante.mccic.qol.client.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.andante.mccic.config.ConfigHolder;
import dev.andante.mccic.qol.MCCICQoL;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.StringIdentifiable;

@Environment(EnvType.CLIENT)
public record QoLClientConfig(ConfirmDisconnectMode confirmDisconnectMode, boolean emptySlotHighlightsFix, boolean eventAnnouncementToast) {
    public static final Codec<QoLClientConfig> CODEC = RecordCodecBuilder.create(
        instance -> {
            QoLClientConfig defaultConfig = createDefaultConfig();
            return instance.group(
                StringIdentifiable.createCodec(ConfirmDisconnectMode::values)
                                  .fieldOf("confirm_disconnect_mode")
                                  .orElse(defaultConfig.confirmDisconnectMode())
                                  .forGetter(QoLClientConfig::confirmDisconnectMode),
                Codec.BOOL.fieldOf("empty_slot_highlights_fix")
                          .orElse(defaultConfig.emptySlotHighlightsFix())
                          .forGetter(QoLClientConfig::emptySlotHighlightsFix),
                Codec.BOOL.fieldOf("event_announcement_toast")
                          .orElse(defaultConfig.eventAnnouncementToast())
                          .forGetter(QoLClientConfig::eventAnnouncementToast)
            ).apply(instance, QoLClientConfig::new);
        }
    );

    public static final ConfigHolder<QoLClientConfig> CONFIG_HOLDER = new ConfigHolder<>(MCCICQoL.ID, CODEC, createDefaultConfig());

    public static QoLClientConfig getConfig() {
        return CONFIG_HOLDER.get();
    }

    public static QoLClientConfig createDefaultConfig() {
        return new QoLClientConfig(ConfirmDisconnectMode.IN_GAME, true, true);
    }
}

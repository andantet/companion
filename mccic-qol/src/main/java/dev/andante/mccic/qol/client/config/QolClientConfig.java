package dev.andante.mccic.qol.client.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.andante.mccic.config.ConfigHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.StringIdentifiable;

@Environment(EnvType.CLIENT)
public record QolClientConfig(ConfirmDisconnectMode confirmDisconnectMode) {
    public static final Codec<QolClientConfig> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            StringIdentifiable.createCodec(ConfirmDisconnectMode::values)
                              .fieldOf("confirm_disconnect_mode")
                              .orElse(createDefaultConfig().confirmDisconnectMode())
                              .forGetter(QolClientConfig::confirmDisconnectMode)
        ).apply(instance, QolClientConfig::new)
    );

    public static final ConfigHolder<QolClientConfig> CONFIG_HOLDER = new ConfigHolder<>("qol", CODEC, createDefaultConfig());

    public static QolClientConfig getConfig() {
        return CONFIG_HOLDER.get();
    }

    public static QolClientConfig createDefaultConfig() {
        return new QolClientConfig(ConfirmDisconnectMode.IN_GAME);
    }
}

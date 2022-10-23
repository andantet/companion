package dev.andante.mccic.keybindings.client.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.andante.mccic.config.ConfigHolder;
import dev.andante.mccic.keybindings.MCCICKeyBindings;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public record KeyBindingsClientConfig(boolean confirmHub) {
    public static final Codec<KeyBindingsClientConfig> CODEC = RecordCodecBuilder.create(
        instance -> {
            KeyBindingsClientConfig defaultConfig = KeyBindingsClientConfig.createDefaultConfig();
            return instance.group(
                Codec.BOOL.fieldOf("confirm_hub")
                          .orElse(defaultConfig.confirmHub())
                          .forGetter(KeyBindingsClientConfig::confirmHub)
            ).apply(instance, KeyBindingsClientConfig::new);
        }
    );

    public static final ConfigHolder<KeyBindingsClientConfig> CONFIG_HOLDER = new ConfigHolder<>(MCCICKeyBindings.ID, CODEC, createDefaultConfig());

    public static KeyBindingsClientConfig getConfig() {
        return CONFIG_HOLDER.get();
    }

    public static KeyBindingsClientConfig createDefaultConfig() {
        return new KeyBindingsClientConfig(true);
    }
}

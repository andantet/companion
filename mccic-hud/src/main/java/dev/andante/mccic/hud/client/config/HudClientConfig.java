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
public record HudClientConfig(boolean playerPreviewInWardrobe, boolean mccicLoadingScreen, boolean autoCloseBetaTestWarning, boolean hideSecureChatEnforcementToast, boolean hudEnabled, HudPosition hudTimerPosition, HudPosition hudQueuePosition) {
    public static final Codec<HudClientConfig> CODEC = RecordCodecBuilder.create(
            instance -> {
                ConfigCodecBuilder<HudClientConfig> builder = new ConfigCodecBuilder<>(HudClientConfig.createDefaultConfig());
                return instance.group(
                        builder.createBool("player_preview_in_wardrobe", HudClientConfig::playerPreviewInWardrobe),
                        builder.createBool("mccic_loading_screen", HudClientConfig::mccicLoadingScreen),
                        builder.createBool("auto_close_beta_test_warning", HudClientConfig::autoCloseBetaTestWarning),
                        builder.createBool("hide_secure_chat_enforcement_toast", HudClientConfig::hideSecureChatEnforcementToast),
                        builder.createBool("hud_enabled", HudClientConfig::hudEnabled),
                        builder.createEnum("hud_timer_position", HudPosition::values, HudClientConfig::hudTimerPosition),
                        builder.createEnum("hud_queue_position", HudPosition::values, HudClientConfig::hudQueuePosition)
                ).apply(instance, HudClientConfig::new);
            }
    );

    public static final ConfigHolder<HudClientConfig> CONFIG_HOLDER = new ConfigHolder<>(MCCICHud.ID, CODEC, createDefaultConfig()).registerSaveListener(MCCIHudRenderer.INSTANCE::refreshElementLists);

    public static HudClientConfig getConfig() {
        return CONFIG_HOLDER.get();
    }

    public static HudClientConfig createDefaultConfig() {
        return new HudClientConfig(true, true, false, false, false, HudPosition.TOP, HudPosition.TOP);
    }
}

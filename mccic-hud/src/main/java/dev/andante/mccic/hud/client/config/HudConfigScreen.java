package dev.andante.mccic.hud.client.config;

import dev.andante.mccic.config.client.screen.AbstractConfigScreen;
import dev.andante.mccic.hud.MCCICHud;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.SimpleOption;

import java.util.List;

@Environment(EnvType.CLIENT)
public class HudConfigScreen extends AbstractConfigScreen<HudClientConfig> {
    public final SimpleOption<Boolean> playerPreviewInWardrobeOption;
    public final SimpleOption<Boolean> mccicLoadingScreenOption;
    public final SimpleOption<Boolean> autoCloseBetaTestWarningOption;
    public final SimpleOption<Boolean> hudEnabledOption;
    public final SimpleOption<HudPosition> hudTimerPositionOption;
    public final SimpleOption<HudPosition> hudQueuePositionOption;

    public HudConfigScreen(Screen parent) {
        super(MCCICHud.MOD_ID, parent, HudClientConfig.CONFIG_HOLDER);
        this.playerPreviewInWardrobeOption = this.ofBooleanTooltip("player_preview_in_wardrobe", HudClientConfig::playerPreviewInWardrobe);
        this.mccicLoadingScreenOption = this.ofBooleanTooltip("mccic_loading_screen", HudClientConfig::mccicLoadingScreen);
        this.autoCloseBetaTestWarningOption = this.ofBooleanTooltip("auto_close_beta_test_warning", HudClientConfig::autoCloseBetaTestWarning);
        this.hudEnabledOption = this.ofBoolean("hud_enabled", HudClientConfig::hudEnabled);
        this.hudTimerPositionOption = this.ofEnum("hud_timer_position", HudPosition::byId, HudPosition.values(), HudClientConfig::hudTimerPosition);
        this.hudQueuePositionOption = this.ofEnum("hud_queue_position", HudPosition::byId, HudPosition.values(), HudClientConfig::hudQueuePosition);
    }

    @Override
    protected List<SimpleOption<?>> getOptions() {
        return List.of(this.playerPreviewInWardrobeOption, this.mccicLoadingScreenOption, this.autoCloseBetaTestWarningOption, this.hudEnabledOption, this.hudTimerPositionOption, this.hudQueuePositionOption);
    }

    @Override
    public HudClientConfig createConfig() {
        return new HudClientConfig(this.playerPreviewInWardrobeOption.getValue(), this.mccicLoadingScreenOption.getValue(), this.autoCloseBetaTestWarningOption.getValue(), this.hudEnabledOption.getValue(), this.hudTimerPositionOption.getValue(), this.hudQueuePositionOption.getValue());
    }

    @Override
    public HudClientConfig getConfig() {
        return HudClientConfig.getConfig();
    }

    @Override
    public HudClientConfig getDefaultConfig() {
        return HudClientConfig.createDefaultConfig();
    }
}

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
    public final SimpleOption<HudMode> topHudOption;
    public final SimpleOption<HudPosition> timerPositionOption;

    public HudConfigScreen(Screen parent) {
        super(MCCICHud.MOD_ID, parent, HudClientConfig.CONFIG_HOLDER);
        this.topHudOption = this.ofEnum("top_hud", HudMode::byId, HudMode.values(), HudClientConfig::topHud);
        this.timerPositionOption = this.ofEnum("timer_position", HudPosition::byId, HudPosition.values(), HudClientConfig::timerPosition);
    }

    @Override
    protected List<SimpleOption<?>> getOptions() {
        return List.of(this.topHudOption, this.timerPositionOption);
    }

    @Override
    public HudClientConfig createConfig() {
        return new HudClientConfig(this.topHudOption.getValue(), this.timerPositionOption.getValue());
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

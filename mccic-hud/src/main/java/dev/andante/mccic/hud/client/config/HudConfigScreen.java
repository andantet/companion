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
    public static final SimpleOption<HudMode> TOP_HUD_OPTION;
    public static final SimpleOption<HudPosition> TIMER_POSITION_OPTION;

    public HudConfigScreen(Screen parent) {
        super(MCCICHud.MOD_ID, parent, HudClientConfig.CONFIG_HOLDER);
    }

    @Override
    protected List<SimpleOption<?>> getOptions() {
        return List.of(TOP_HUD_OPTION, TIMER_POSITION_OPTION);
    }

    @Override
    public HudClientConfig createConfig() {
        return new HudClientConfig(TOP_HUD_OPTION.getValue(), TIMER_POSITION_OPTION.getValue());
    }

    static {
        HudClientConfig config = HudClientConfig.getConfig();
        HudClientConfig defaultConfig = HudClientConfig.createDefaultConfig();
        TOP_HUD_OPTION = ofEnum(MCCICHud.MOD_ID, "top_hud", HudMode::byId, HudMode.values(), config, defaultConfig, HudClientConfig::topHud);
        TIMER_POSITION_OPTION = ofEnum(MCCICHud.MOD_ID, "timer_position", HudPosition::byId, HudPosition.values(), config, defaultConfig, HudClientConfig::timerPosition);
    }
}

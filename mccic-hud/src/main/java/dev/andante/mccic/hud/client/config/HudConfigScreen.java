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
    public final SimpleOption<Boolean> enabledOption;
    public final SimpleOption<HudPosition> timerPositionOption;
    public final SimpleOption<HudPosition> queuePositionOption;

    public HudConfigScreen(Screen parent) {
        super(MCCICHud.MOD_ID, parent, HudClientConfig.CONFIG_HOLDER);
        this.enabledOption = this.ofBoolean("enabled", HudClientConfig::enabled);
        this.timerPositionOption = this.ofEnum("timer_position", HudPosition::byId, HudPosition.values(), HudClientConfig::timerPosition);
        this.queuePositionOption = this.ofEnum("queue_position", HudPosition::byId, HudPosition.values(), HudClientConfig::queuePosition);
    }

    @Override
    protected List<SimpleOption<?>> getOptions() {
        return List.of(this.enabledOption, this.timerPositionOption, this.queuePositionOption);
    }

    @Override
    public HudClientConfig createConfig() {
        return new HudClientConfig(this.enabledOption.getValue(), this.timerPositionOption.getValue(), this.queuePositionOption.getValue());
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

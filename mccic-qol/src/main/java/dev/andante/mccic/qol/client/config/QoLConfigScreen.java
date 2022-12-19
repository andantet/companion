package dev.andante.mccic.qol.client.config;

import dev.andante.mccic.config.client.screen.AbstractConfigScreen;
import dev.andante.mccic.qol.MCCICQoL;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.option.SimpleOption.TooltipFactory;
import net.minecraft.text.Text;

import java.util.List;

@Environment(EnvType.CLIENT)
public class QoLConfigScreen extends AbstractConfigScreen<QoLClientConfig> {
    public final SimpleOption<ConfirmDisconnectMode> confirmDisconnectModeOption;
    public final SimpleOption<GlowingMode> glowingModeOption;
    public final SimpleOption<Boolean> emptySlotHighlightsFixOption;
    public final SimpleOption<Boolean> extendedFrustumsOption;
    public final SimpleOption<Boolean> autoHitboxSkyBattleOption;
    public final SimpleOption<Boolean> autoHitboxBattleBoxOption;

    public QoLConfigScreen(Screen parent) {
        super(MCCICQoL.MOD_ID, parent, QoLClientConfig.CONFIG_HOLDER);

        this.confirmDisconnectModeOption = this.ofEnum("confirm_disconnect_mode", ConfirmDisconnectMode::byId, ConfirmDisconnectMode.values(), QoLClientConfig::confirmDisconnectMode);
        this.glowingModeOption = this.ofEnum("glowing_mode", GlowingMode::byId, GlowingMode.values(), QoLClientConfig::glowingMode);
        this.emptySlotHighlightsFixOption = this.ofBoolean("empty_slot_highlights_fix", QoLClientConfig::emptySlotHighlightsFix);
        this.extendedFrustumsOption = this.ofBooleanTooltip("extended_frustums", QoLClientConfig::extendedFrustums);

        TooltipFactory<Boolean> autoHitboxTooltip = SimpleOption.constantTooltip(Text.translatable(this.createConfigTranslationKey("auto_hitbox.tooltip")));
        this.autoHitboxSkyBattleOption = this.ofBoolean("auto_hitbox_sky_battle", QoLClientConfig::autoHitboxSkyBattle, autoHitboxTooltip);
        this.autoHitboxBattleBoxOption = this.ofBoolean("auto_hitbox_battle_box", QoLClientConfig::autoHitboxBattleBox, autoHitboxTooltip);
    }

    @Override
    protected List<SimpleOption<?>> getOptions() {
        return List.of(this.confirmDisconnectModeOption, this.glowingModeOption, this.emptySlotHighlightsFixOption, this.extendedFrustumsOption, this.autoHitboxSkyBattleOption, this.autoHitboxBattleBoxOption);
    }

    @Override
    public QoLClientConfig createConfig() {
        return new QoLClientConfig(this.confirmDisconnectModeOption.getValue(), this.glowingModeOption.getValue(), this.emptySlotHighlightsFixOption.getValue(), this.extendedFrustumsOption.getValue(), this.autoHitboxSkyBattleOption.getValue(), this.autoHitboxBattleBoxOption.getValue());
    }

    @Override
    public QoLClientConfig getConfig() {
        return QoLClientConfig.getConfig();
    }

    @Override
    public QoLClientConfig getDefaultConfig() {
        return QoLClientConfig.createDefaultConfig();
    }
}

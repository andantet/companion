package dev.andante.mccic.qol.client.config;

import dev.andante.mccic.config.client.screen.AbstractConfigScreen;
import dev.andante.mccic.qol.MCCICQoL;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.SimpleOption;

import java.util.List;

public class QoLConfigScreen extends AbstractConfigScreen<QoLClientConfig> {
    public static final SimpleOption<ConfirmDisconnectMode> CONFIRM_DISCONNECT_MODE_OPTION;
    public static final SimpleOption<Boolean> EMPTY_SLOT_HIGHLIGHTS_FIX_OPTION;
    public static final SimpleOption<Boolean> EVENT_ANNOUNCEMENT_TOAST_OPTION;
    public static final SimpleOption<Boolean> EXTENDED_FRUSTUMS_OPTION;
    public static final SimpleOption<Boolean> AUTO_HITBOX_SKY_BATTLE_OPTION;
    public static final SimpleOption<Boolean> AUTO_HITBOX_BATTLE_BOX_OPTION;

    public QoLConfigScreen(Screen parent) {
        super(MCCICQoL.MOD_ID, parent, QoLClientConfig.CONFIG_HOLDER);
    }

    @Override
    protected List<SimpleOption<?>> getOptions() {
        return List.of(CONFIRM_DISCONNECT_MODE_OPTION, EMPTY_SLOT_HIGHLIGHTS_FIX_OPTION, EVENT_ANNOUNCEMENT_TOAST_OPTION, EXTENDED_FRUSTUMS_OPTION, AUTO_HITBOX_SKY_BATTLE_OPTION, AUTO_HITBOX_BATTLE_BOX_OPTION);
    }

    @Override
    public QoLClientConfig createConfig() {
        return new QoLClientConfig(CONFIRM_DISCONNECT_MODE_OPTION.getValue(), EMPTY_SLOT_HIGHLIGHTS_FIX_OPTION.getValue(), EVENT_ANNOUNCEMENT_TOAST_OPTION.getValue(), EXTENDED_FRUSTUMS_OPTION.getValue(), AUTO_HITBOX_SKY_BATTLE_OPTION.getValue(), AUTO_HITBOX_BATTLE_BOX_OPTION.getValue());
    }

    static {
        QoLClientConfig config = QoLClientConfig.getConfig();
        QoLClientConfig defaultConfig = QoLClientConfig.createDefaultConfig();
        CONFIRM_DISCONNECT_MODE_OPTION = ofEnum(MCCICQoL.MOD_ID, "confirm_disconnect_mode", ConfirmDisconnectMode::byId, ConfirmDisconnectMode.values(), config.confirmDisconnectMode(), defaultConfig.confirmDisconnectMode());
        EMPTY_SLOT_HIGHLIGHTS_FIX_OPTION = ofBoolean(MCCICQoL.MOD_ID, "empty_slot_highlights_fix", config.emptySlotHighlightsFix(), defaultConfig.emptySlotHighlightsFix());
        EVENT_ANNOUNCEMENT_TOAST_OPTION = ofBoolean(MCCICQoL.MOD_ID, "event_announcement_toast", config.eventAnnouncementToast(), defaultConfig.eventAnnouncementToast());
        EXTENDED_FRUSTUMS_OPTION = ofBoolean(MCCICQoL.MOD_ID, "extended_frustums", config.extendedFrustums(), defaultConfig.extendedFrustums());
        AUTO_HITBOX_SKY_BATTLE_OPTION = ofBoolean(MCCICQoL.MOD_ID, "auto_hitbox_sky_battle", config.autoHitboxSkyBattle(), defaultConfig.autoHitboxSkyBattle());
        AUTO_HITBOX_BATTLE_BOX_OPTION = ofBoolean(MCCICQoL.MOD_ID, "auto_hitbox_battle_box", config.autoHitboxBattleBox(), defaultConfig.autoHitboxBattleBox());
    }
}

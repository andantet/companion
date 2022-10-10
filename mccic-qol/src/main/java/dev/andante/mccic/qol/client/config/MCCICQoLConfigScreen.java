package dev.andante.mccic.qol.client.config;

import dev.andante.mccic.config.client.screen.MCCICAbstractConfigScreen;
import dev.andante.mccic.qol.MCCICQoL;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.SimpleOption;

public class MCCICQoLConfigScreen extends MCCICAbstractConfigScreen<QoLClientConfig> {
    public static final SimpleOption<ConfirmDisconnectMode> CONFIRM_DISCONNECT_MODE_OPTION;
    public static final SimpleOption<Boolean> EMPTY_SLOT_HIGHLIGHTS_FIX_OPTION;
    public static final SimpleOption<Boolean> EVENT_ANNOUNCEMENT_TOAST_OPTION;

    public MCCICQoLConfigScreen(Screen parent) {
        super(MCCICQoL.MOD_ID, parent, QoLClientConfig.CONFIG_HOLDER);
    }

    @Override
    protected void init() {
        super.init();
        this.list.addSingleOptionEntry(CONFIRM_DISCONNECT_MODE_OPTION);
        this.list.addSingleOptionEntry(EMPTY_SLOT_HIGHLIGHTS_FIX_OPTION);
        this.list.addSingleOptionEntry(EVENT_ANNOUNCEMENT_TOAST_OPTION);
    }

    @Override
    protected void saveConfig() {
        QoLClientConfig.CONFIG_HOLDER.set(new QoLClientConfig(CONFIRM_DISCONNECT_MODE_OPTION.getValue(), EMPTY_SLOT_HIGHLIGHTS_FIX_OPTION.getValue(), EVENT_ANNOUNCEMENT_TOAST_OPTION.getValue()));
        super.saveConfig();
    }

    static {
        QoLClientConfig config = QoLClientConfig.getConfig();
        QoLClientConfig defaultConfig = QoLClientConfig.createDefaultConfig();
        CONFIRM_DISCONNECT_MODE_OPTION = ofEnum(MCCICQoL.MOD_ID, "confirm_disconnect_mode", ConfirmDisconnectMode::byId, ConfirmDisconnectMode.values(), config.confirmDisconnectMode(), defaultConfig.confirmDisconnectMode());
        EMPTY_SLOT_HIGHLIGHTS_FIX_OPTION = ofBoolean(MCCICQoL.MOD_ID, "empty_slot_highlights_fix", config.emptySlotHighlightsFix(), defaultConfig.emptySlotHighlightsFix());
        EVENT_ANNOUNCEMENT_TOAST_OPTION = ofBoolean(MCCICQoL.MOD_ID, "event_announcement_toast", config.eventAnnouncementToast(), defaultConfig.eventAnnouncementToast());
    }
}

package dev.andante.mccic.toasts.client.config;

import dev.andante.mccic.config.client.screen.AbstractConfigScreen;
import dev.andante.mccic.toasts.MCCICToasts;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.SimpleOption;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ToastsConfigScreen extends AbstractConfigScreen<ToastsClientConfig> {
    public final SimpleOption<Boolean> friendsOption;
    public final SimpleOption<Boolean> partiesOption;
    public final SimpleOption<Boolean> questsOption;
    public final SimpleOption<Boolean> achievementsOption;
    public final SimpleOption<Boolean> eventAnnouncementsOption;
    public final SimpleOption<Boolean> updateNotificationsOption;

    public ToastsConfigScreen(Screen parent) {
        super(MCCICToasts.MOD_ID, parent, ToastsClientConfig.CONFIG_HOLDER);
        this.friendsOption = this.ofBoolean("friends", ToastsClientConfig::friends);
        this.partiesOption = this.ofBoolean("parties", ToastsClientConfig::parties);
        this.questsOption = this.ofBoolean("quests", ToastsClientConfig::quests);
        this.achievementsOption = this.ofBoolean("achievements", ToastsClientConfig::achievements);
        this.eventAnnouncementsOption = this.ofBooleanTooltip("event_announcements", ToastsClientConfig::eventAnnouncements);
        this.updateNotificationsOption = this.ofBooleanTooltip("update_notifications", ToastsClientConfig::updateNotifications);
    }

    @Override
    protected List<SimpleOption<?>> getOptions() {
        return List.of(this.friendsOption, this.partiesOption, this.questsOption, this.eventAnnouncementsOption, this.updateNotificationsOption);
    }

    @Override
    public ToastsClientConfig createConfig() {
        return new ToastsClientConfig(this.friendsOption.getValue(), this.partiesOption.getValue(), this.questsOption.getValue(), this.achievementsOption.getValue(), this.eventAnnouncementsOption.getValue(), this.updateNotificationsOption.getValue());
    }

    @Override
    public ToastsClientConfig getConfig() {
        return ToastsClientConfig.getConfig();
    }

    @Override
    public ToastsClientConfig getDefaultConfig() {
        return ToastsClientConfig.createDefaultConfig();
    }
}

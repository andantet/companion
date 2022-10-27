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
    public static final SimpleOption<Boolean> FRIENDS_OPTION;
    public static final SimpleOption<Boolean> PARTIES_OPTION;
    public static final SimpleOption<Boolean> QUESTS_OPTION;
    public static final SimpleOption<Boolean> ACHIEVEMENTS_OPTION;
    public static final SimpleOption<Boolean> EVENT_ANNOUNCEMENTS_OPTION;

    public ToastsConfigScreen(Screen parent) {
        super(MCCICToasts.MOD_ID, parent, ToastsClientConfig.CONFIG_HOLDER);
    }

    @Override
    protected List<SimpleOption<?>> getOptions() {
        return List.of(FRIENDS_OPTION, PARTIES_OPTION, QUESTS_OPTION, ACHIEVEMENTS_OPTION, EVENT_ANNOUNCEMENTS_OPTION);
    }

    @Override
    public ToastsClientConfig createConfig() {
        return new ToastsClientConfig(FRIENDS_OPTION.getValue(), PARTIES_OPTION.getValue(), QUESTS_OPTION.getValue(), ACHIEVEMENTS_OPTION.getValue(), EVENT_ANNOUNCEMENTS_OPTION.getValue());
    }

    static {
        ToastsClientConfig config = ToastsClientConfig.getConfig();
        ToastsClientConfig defaultConfig = ToastsClientConfig.createDefaultConfig();
        FRIENDS_OPTION = ofBoolean(MCCICToasts.MOD_ID, "friends", config.friends(), defaultConfig.friends());
        PARTIES_OPTION = ofBoolean(MCCICToasts.MOD_ID, "parties", config.parties(), defaultConfig.parties());
        QUESTS_OPTION = ofBoolean(MCCICToasts.MOD_ID, "quests", config.quests(), defaultConfig.quests());
        ACHIEVEMENTS_OPTION = ofBoolean(MCCICToasts.MOD_ID, "achievements", config.achievements(), defaultConfig.achievements());
        EVENT_ANNOUNCEMENTS_OPTION = ofBoolean(MCCICToasts.MOD_ID, "event_announcements", config.eventAnnouncements(), defaultConfig.eventAnnouncements());
    }
}

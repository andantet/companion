package dev.andante.mccic.social.client.config;

import dev.andante.mccic.config.client.screen.AbstractConfigScreen;
import dev.andante.mccic.social.MCCICSocial;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.SimpleOption;

public class SocialConfigScreen extends AbstractConfigScreen<SocialClientConfig> {
    public static final SimpleOption<Boolean> FRIEND_TOASTS_OPTION;
    public static final SimpleOption<Boolean> PARTY_TOASTS_OPTION;

    public SocialConfigScreen(Screen parent) {
        super(MCCICSocial.MOD_ID, parent, SocialClientConfig.CONFIG_HOLDER);
    }

    @Override
    protected void init() {
        super.init();
        this.list.addSingleOptionEntry(FRIEND_TOASTS_OPTION);
        this.list.addSingleOptionEntry(PARTY_TOASTS_OPTION);
    }

    @Override
    protected void saveConfig() {
        SocialClientConfig.CONFIG_HOLDER.set(new SocialClientConfig(FRIEND_TOASTS_OPTION.getValue(), PARTY_TOASTS_OPTION.getValue()));
        super.saveConfig();
    }

    static {
        SocialClientConfig config = SocialClientConfig.getConfig();
        SocialClientConfig defaultConfig = SocialClientConfig.createDefaultConfig();
        FRIEND_TOASTS_OPTION = ofBoolean(MCCICSocial.MOD_ID, "friend_toasts", config.friendToasts(), defaultConfig.friendToasts());
        PARTY_TOASTS_OPTION = ofBoolean(MCCICSocial.MOD_ID, "party_toasts", config.partyToasts(), defaultConfig.partyToasts());
    }
}

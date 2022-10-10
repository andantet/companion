package dev.andante.mccic.social.client.config;

import dev.andante.mccic.config.client.screen.MCCICAbstractConfigScreen;
import dev.andante.mccic.social.MCCICSocial;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.SimpleOption;

public class MCCICSocialConfigScreen extends MCCICAbstractConfigScreen<SocialClientConfig> {
    public static final SimpleOption<Boolean> ALL_TOASTS_OPTION;

    public MCCICSocialConfigScreen(Screen parent) {
        super(MCCICSocial.MOD_ID, parent, SocialClientConfig.CONFIG_HOLDER);
    }

    @Override
    protected void init() {
        super.init();
        this.list.addSingleOptionEntry(ALL_TOASTS_OPTION);
    }

    @Override
    protected void saveConfig() {
        SocialClientConfig.CONFIG_HOLDER.set(new SocialClientConfig(ALL_TOASTS_OPTION.getValue()));
        super.saveConfig();
    }

    static {
        SocialClientConfig defaultConfig = SocialClientConfig.createDefaultConfig();
        ALL_TOASTS_OPTION = ofBoolean(MCCICSocial.MOD_ID, "all_toasts", defaultConfig.allToasts());
    }
}

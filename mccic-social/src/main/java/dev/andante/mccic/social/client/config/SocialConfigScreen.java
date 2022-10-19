package dev.andante.mccic.social.client.config;

import dev.andante.mccic.config.client.screen.AbstractConfigScreen;
import dev.andante.mccic.social.MCCICSocial;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.SimpleOption;

import java.util.List;

@Environment(EnvType.CLIENT)
public class SocialConfigScreen extends AbstractConfigScreen<SocialClientConfig> {
    public static final SimpleOption<Boolean> FRIEND_TOASTS_OPTION;
    public static final SimpleOption<Boolean> PARTY_TOASTS_OPTION;
    public static final SimpleOption<HubPlayerRenderMode> HUB_PLAYER_RENDER_MODE_OPTION;

    public SocialConfigScreen(Screen parent) {
        super(MCCICSocial.MOD_ID, parent, SocialClientConfig.CONFIG_HOLDER);
    }

    @Override
    protected List<SimpleOption<?>> getOptions() {
        return List.of(FRIEND_TOASTS_OPTION, PARTY_TOASTS_OPTION/*, HUB_PLAYER_RENDER_MODE_OPTION*/);
    }

    @Override
    public SocialClientConfig createConfig() {
        return new SocialClientConfig(FRIEND_TOASTS_OPTION.getValue(), PARTY_TOASTS_OPTION.getValue(), HUB_PLAYER_RENDER_MODE_OPTION.getValue());
    }

    static {
        SocialClientConfig config = SocialClientConfig.getConfig();
        SocialClientConfig defaultConfig = SocialClientConfig.createDefaultConfig();
        FRIEND_TOASTS_OPTION = ofBoolean(MCCICSocial.MOD_ID, "friend_toasts", config.friendToasts(), defaultConfig.friendToasts());
        PARTY_TOASTS_OPTION = ofBoolean(MCCICSocial.MOD_ID, "party_toasts", config.partyToasts(), defaultConfig.partyToasts());
        HUB_PLAYER_RENDER_MODE_OPTION = ofEnum(MCCICSocial.MOD_ID, "hub_player_render_mode", HubPlayerRenderMode::byId, HubPlayerRenderMode.values(), config.hubPlayerRenderMode(), defaultConfig.hubPlayerRenderMode());
    }
}

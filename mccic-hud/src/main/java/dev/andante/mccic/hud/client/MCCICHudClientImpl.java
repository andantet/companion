package dev.andante.mccic.hud.client;

import dev.andante.mccic.api.client.UnicodeIconsStore;
import dev.andante.mccic.api.client.UnicodeIconsStore.Icon;
import dev.andante.mccic.api.util.MCCIFont;
import dev.andante.mccic.api.util.TextQuery;
import dev.andante.mccic.config.client.ClientConfigRegistry;
import dev.andante.mccic.config.client.command.MCCICConfigCommand;
import dev.andante.mccic.hud.MCCICHud;
import dev.andante.mccic.hud.client.config.HudClientConfig;
import dev.andante.mccic.hud.client.config.HudConfigScreen;
import dev.andante.mccic.hud.client.render.MCCIHudRenderer;
import dev.andante.mccic.hud.client.render.WardrobeHudRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public final class MCCICHudClientImpl implements MCCICHud, ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientConfigRegistry.INSTANCE.registerAndLoad(HudClientConfig.CONFIG_HOLDER, HudConfigScreen::new);
        MCCIHudRenderer.INSTANCE.refreshElementLists(HudClientConfig.CONFIG_HOLDER);
        MCCICConfigCommand.registerNewConfig(ID, HudConfigScreen::new);

        ScreenEvents.AFTER_INIT.register(this::afterScreenInit);
    }

    private void afterScreenInit(MinecraftClient client, Screen screen, int scaledWidth, int scaledHeight) {
        Text title = screen.getTitle();
        if (UnicodeIconsStore.doesTextContainIconExactFont(title, Icon.GUI_BETA_TEST_WARNING, MCCIFont.GUI)) {
            if (HudClientConfig.getConfig().autoCloseBetaTestWarning()) {
                client.send(() -> client.player.closeHandledScreen());
            }
        } else if (TextQuery.findText(title, WardrobeHudRenderer.createGuiWardrobeTextPattern()).isPresent()) {
            ScreenEvents.afterRender(screen).register(WardrobeHudRenderer.INSTANCE::render);
        }
    }
}

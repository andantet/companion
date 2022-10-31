package dev.andante.mccic.hud.client;

import dev.andante.mccic.config.client.ClientConfigRegistry;
import dev.andante.mccic.config.client.command.MCCICConfigCommand;
import dev.andante.mccic.hud.MCCICHud;
import dev.andante.mccic.hud.client.config.HudClientConfig;
import dev.andante.mccic.hud.client.config.HudConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class MCCICHudClientImpl implements MCCICHud, ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientConfigRegistry.INSTANCE.registerAndLoad(HudClientConfig.CONFIG_HOLDER, HudConfigScreen::new);
        MCCICConfigCommand.registerNewConfig(ID, HudConfigScreen::new);
    }
}

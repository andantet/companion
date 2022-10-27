package dev.andante.mccic.qol.client;

import dev.andante.mccic.config.client.ClientConfigRegistry;
import dev.andante.mccic.config.client.command.MCCICConfigCommand;
import dev.andante.mccic.qol.MCCICQoL;
import dev.andante.mccic.qol.client.config.QoLClientConfig;
import dev.andante.mccic.qol.client.config.QoLConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class MCCICQolClientImpl implements MCCICQoL, ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientConfigRegistry.INSTANCE.registerAndLoad(QoLClientConfig.CONFIG_HOLDER, QoLConfigScreen::new);
        MCCICConfigCommand.registerNewConfig(ID, QoLConfigScreen::new);
    }
}

package dev.andante.mccic.qol.client;

import dev.andante.mccic.api.MCCIC;
import dev.andante.mccic.config.client.ClientConfigRegistry;
import dev.andante.mccic.qol.client.config.QolClientConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class MCCICQolClientImpl implements MCCIC, ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientConfigRegistry.INSTANCE.registerAndLoad(QolClientConfig.CONFIG_HOLDER);
    }
}

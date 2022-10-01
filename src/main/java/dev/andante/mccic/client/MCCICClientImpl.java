package dev.andante.mccic.client;

import dev.andante.mccic.MCCIC;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class MCCICClientImpl implements MCCIC, ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing {}", MOD_NAME);
    }
}

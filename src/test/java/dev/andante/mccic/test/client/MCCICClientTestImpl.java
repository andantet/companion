package dev.andante.mccic.test.client;

import dev.andante.mccic.api.MCCIC;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class MCCICClientTestImpl implements MCCIC, ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing {}-TEST", MOD_NAME);
    }
}

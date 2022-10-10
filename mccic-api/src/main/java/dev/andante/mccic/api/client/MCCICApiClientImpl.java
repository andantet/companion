package dev.andante.mccic.api.client;

import com.google.common.reflect.Reflection;
import dev.andante.mccic.api.MCCICApi;
import dev.andante.mccic.api.client.game.GameTracker;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@SuppressWarnings("UnstableApiUsage")
@Environment(EnvType.CLIENT)
public final class MCCICApiClientImpl implements MCCICApi, ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing {}", MOD_NAME);
        Reflection.initialize(GameTracker.class, MCCICUpdateTracker.class);
    }
}

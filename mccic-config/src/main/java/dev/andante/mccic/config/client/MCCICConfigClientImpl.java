package dev.andante.mccic.config.client;

import dev.andante.mccic.api.MCCIC;
import dev.andante.mccic.config.client.command.MCCICConfigCommand;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

@Environment(EnvType.CLIENT)
public final class MCCICConfigClientImpl implements MCCIC, ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            MCCICConfigCommand.register(dispatcher);
        });
    }
}

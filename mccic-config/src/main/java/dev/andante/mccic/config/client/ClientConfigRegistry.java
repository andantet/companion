package dev.andante.mccic.config.client;

import dev.andante.mccic.config.ConfigRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClientConfigRegistry extends ConfigRegistry {
    public static final ClientConfigRegistry INSTANCE = new ClientConfigRegistry();

    public ClientConfigRegistry() {
    }
}

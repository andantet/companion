package dev.andante.mccic.qol.client;

import dev.andante.mccic.config.client.ClientConfigRegistry;
import dev.andante.mccic.config.client.command.MCCICConfigCommand;
import dev.andante.mccic.qol.MCCICQoL;
import dev.andante.mccic.qol.client.config.QoLClientConfig;
import dev.andante.mccic.qol.client.config.QoLConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public final class MCCICQolClientImpl implements MCCICQoL, ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientConfigRegistry.INSTANCE.registerAndLoad(QoLClientConfig.CONFIG_HOLDER, QoLConfigScreen::new);
        MCCICConfigCommand.registerNewConfig(ID, QoLConfigScreen::new);

        FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(container -> {
            Identifier id = new Identifier(container.getMetadata().getId(), "american_date_format");
            ResourceManagerHelper.registerBuiltinResourcePack(id, container, "MCCIC: American Date Format", ResourcePackActivationType.NORMAL);
        });
    }
}

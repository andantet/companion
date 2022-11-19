package dev.andante.mccic.api.client;

import com.google.common.reflect.Reflection;
import dev.andante.mccic.api.MCCICApi;
import dev.andante.mccic.api.client.tracker.ChatModeTracker;
import dev.andante.mccic.api.client.tracker.GameTracker;
import dev.andante.mccic.api.client.tracker.QueueTracker;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

@SuppressWarnings("UnstableApiUsage")
@Environment(EnvType.CLIENT)
public final class MCCICApiClientImpl implements MCCICApi, ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing {}", MOD_NAME);

        Reflection.initialize(GameTracker.class, QueueTracker.class, UpdateTracker.class, ChatModeTracker.class);

        FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(container -> {
            Identifier id = new Identifier(container.getMetadata().getId(), "american_date_format");
            ResourceManagerHelper.registerBuiltinResourcePack(id, container, "MCCIC: American Date Format", ResourcePackActivationType.NORMAL);
        });
    }
}

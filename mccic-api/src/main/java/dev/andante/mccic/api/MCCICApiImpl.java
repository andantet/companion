package dev.andante.mccic.api;

import com.google.common.reflect.Reflection;
import dev.andante.mccic.api.client.UnicodeIconsStore;
import net.fabricmc.api.ModInitializer;

@SuppressWarnings("UnstableApiUsage")
public final class MCCICApiImpl implements MCCICApi, ModInitializer {
    @Override
    public void onInitialize() {
        Reflection.initialize(UnicodeIconsStore.class);
    }
}

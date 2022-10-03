package dev.andante.mccic.config.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.andante.mccic.api.MCCIC;
import dev.andante.mccic.config.client.screen.MCCICConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Map;

@Environment(EnvType.CLIENT)
public final class MCCICConfigModMenuImpl implements MCCIC, ModMenuApi {
    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        return Map.of(MOD_ID, MCCICConfigScreen::new);
    }
}

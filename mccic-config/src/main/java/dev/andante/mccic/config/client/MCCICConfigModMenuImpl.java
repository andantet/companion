package dev.andante.mccic.config.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.andante.mccic.api.MCCIC;
import dev.andante.mccic.config.client.screen.ConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public final class MCCICConfigModMenuImpl implements MCCIC, ModMenuApi {
    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        Map<String, ConfigScreenFactory<?>> map = new HashMap<>();
        map.put(MOD_ID, ConfigScreen::new);
        ClientConfigRegistry.INSTANCE.forEachScreen((holder, factory) -> map.put("%s-%s".formatted(MCCIC.MOD_ID, holder.getModule()), factory::apply));
        return map;
    }
}

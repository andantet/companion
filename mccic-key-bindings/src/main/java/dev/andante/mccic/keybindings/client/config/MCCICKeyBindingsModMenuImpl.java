package dev.andante.mccic.keybindings.client.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.andante.mccic.keybindings.MCCICKeyBindings;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class MCCICKeyBindingsModMenuImpl implements MCCICKeyBindings, ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return KeyBindingsConfigScreen::new;
    }
}

package dev.andante.mccic.discordrp.client.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.andante.mccic.discordrp.MCCICDiscordRP;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class MCCICDiscordRPModMenuImpl implements MCCICDiscordRP, ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return MCCICDiscordRPConfigScreen::new;
    }
}

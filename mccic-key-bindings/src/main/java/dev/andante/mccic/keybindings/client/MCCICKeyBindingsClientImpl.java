package dev.andante.mccic.keybindings.client;

import dev.andante.mccic.config.client.ClientConfigRegistry;
import dev.andante.mccic.config.client.command.MCCICConfigCommand;
import dev.andante.mccic.keybindings.MCCICKeyBindings;
import dev.andante.mccic.keybindings.client.config.KeyBindingsClientConfig;
import dev.andante.mccic.keybindings.client.config.KeyBindingsConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.KeyBinding;

@Environment(EnvType.CLIENT)
public final class MCCICKeyBindingsClientImpl implements MCCICKeyBindings, ClientModInitializer {
    public static final KeyBinding NO = null;

    @Override
    public void onInitializeClient() {
        ClientConfigRegistry.INSTANCE.registerAndLoad(KeyBindingsClientConfig.CONFIG_HOLDER);
        MCCICConfigCommand.registerNewConfig(ID, KeyBindingsConfigScreen::new);
    }
}

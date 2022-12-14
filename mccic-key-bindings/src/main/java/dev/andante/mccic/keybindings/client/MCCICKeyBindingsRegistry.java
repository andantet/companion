package dev.andante.mccic.keybindings.client;

import dev.andante.mccic.keybindings.MCCICKeyBindings;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

@Environment(EnvType.CLIENT)
public interface MCCICKeyBindingsRegistry {
    String CATEGORY = "key.category.%s".formatted(MCCICKeyBindings.MOD_ID);

    KeyBinding TOGGLE_CHAT_MODE = register("toggle_chat_mode", InputUtil.UNKNOWN_KEY.getCode());
    KeyBinding LEAVE_QUEUE = register("leave_queue", InputUtil.UNKNOWN_KEY.getCode());

    private static KeyBinding register(String id, int code) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding("key.%s.%s".formatted(MCCICKeyBindings.MOD_ID, id), code, CATEGORY));
    }
}

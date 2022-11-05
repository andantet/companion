package dev.andante.mccic.chat.client.config;

import dev.andante.mccic.chat.MCCICChat;
import dev.andante.mccic.config.client.screen.AbstractConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.SimpleOption;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ChatConfigScreen extends AbstractConfigScreen<ChatClientConfig> {
    public final SimpleOption<Boolean> mentionsOption;

    public ChatConfigScreen(Screen parent) {
        super(MCCICChat.MOD_ID, parent, ChatClientConfig.CONFIG_HOLDER);
        this.mentionsOption = this.ofBoolean("mentions", ChatClientConfig::mentions);
    }

    @Override
    protected List<SimpleOption<?>> getOptions() {
        return List.of(this.mentionsOption);
    }

    @Override
    public ChatClientConfig createConfig() {
        ChatClientConfig config = this.getConfig();
        return new ChatClientConfig(this.mentionsOption.getValue(), config.mentionsColor());
    }

    @Override
    public ChatClientConfig getConfig() {
        return ChatClientConfig.getConfig();
    }

    @Override
    public ChatClientConfig getDefaultConfig() {
        return ChatClientConfig.createDefaultConfig();
    }
}

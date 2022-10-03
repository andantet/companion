package dev.andante.mccic.debug.client;

import dev.andante.mccic.api.MCCIC;
import dev.andante.mccic.api.client.game.GameTracker;
import dev.andante.mccic.api.game.Game;
import dev.andante.mccic.config.ConfigHolder;
import dev.andante.mccic.config.client.ClientConfigRegistry;
import dev.andante.mccic.debug.client.config.DebugClientConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public final class MCCICDebugClientImpl implements MCCIC, ClientModInitializer {
    public static final ConfigHolder<DebugClientConfig> CONFIG_HOLDER = new ConfigHolder<>("debug", DebugClientConfig.CODEC, DebugClientConfig.createDefaultConfig());

    @Override
    public void onInitializeClient() {
        ClientConfigRegistry.INSTANCE.registerAndLoad(CONFIG_HOLDER);
        HudRenderCallback.EVENT.register(this::onHudRender);
    }

    public static DebugClientConfig getConfig() {
        return CONFIG_HOLDER.get();
    }

    private void onHudRender(MatrixStack matrices, float tickDelta) {
        if (!getConfig().debugHud()) {
            return;
        }

        GameTracker tracker = GameTracker.INSTANCE;
        if (!tracker.isOnServer()) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        client.textRenderer.draw(matrices, Text.of("" + tracker.getGameState().name()), 4, 4, 0xFFFFFF);

        Game game = tracker.getGame();
        if (game != null) {
            client.textRenderer.draw(matrices, Text.of("" + game.asString()), 4, 14, 0xFFFFFF);
        }

        tracker.getTime().ifPresent(time -> {
            client.textRenderer.draw(matrices, Text.of("" + time), 4, 24, 0xFFFFFF);
        });
    }
}

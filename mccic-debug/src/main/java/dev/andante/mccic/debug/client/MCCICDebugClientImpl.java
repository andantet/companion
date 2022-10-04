package dev.andante.mccic.debug.client;

import dev.andante.mccic.api.MCCIC;
import dev.andante.mccic.api.client.game.GameTracker;
import dev.andante.mccic.api.game.Game;
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
    @Override
    public void onInitializeClient() {
        ClientConfigRegistry.INSTANCE.registerAndLoad(DebugClientConfig.CONFIG_HOLDER);
        HudRenderCallback.EVENT.register(this::onHudRender);
    }

    private void onHudRender(MatrixStack matrices, float tickDelta) {
        if (!DebugClientConfig.getConfig().debugHud()) {
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

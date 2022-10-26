package dev.andante.mccic.debug.client;

import dev.andante.mccic.api.client.event.MCCIChatEvent;
import dev.andante.mccic.api.client.game.GameTracker;
import dev.andante.mccic.api.event.EventResult;
import dev.andante.mccic.api.game.Game;
import dev.andante.mccic.config.client.ClientConfigRegistry;
import dev.andante.mccic.debug.MCCICDebug;
import dev.andante.mccic.debug.client.config.DebugClientConfig;
import dev.andante.mccic.debug.client.config.DebugConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public final class MCCICDebugClientImpl implements MCCICDebug, ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientConfigRegistry.INSTANCE.registerAndLoad(DebugClientConfig.CONFIG_HOLDER, DebugConfigScreen::new);
        HudRenderCallback.EVENT.register(this::onHudRender);
        MCCIChatEvent.EVENT.register(this::onChatMessage);
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

    private EventResult onChatMessage(MCCIChatEvent.Context context) {
        if (!DebugClientConfig.getConfig().rawChat()) {
            return EventResult.pass();
        }

        Text message = context.message();
        if (message.getString().startsWith("\u0000")) {
            return EventResult.pass();
        }

        PlayerEntity player = MinecraftClient.getInstance().player;
        player.sendMessage(Text.literal("" + message.getContent().getClass()), true);
        player.sendMessage(Text.literal("\u0000 " + message));

        printText(message);

        return EventResult.cancel();
    }

    private void printText(Text text) {
        if (text.getContent() instanceof LiteralTextContent content) {
            LOGGER.info(content.string());
        }

        for (Text sibling : text.getSiblings()) {
            printText(sibling);
        }
    }
}

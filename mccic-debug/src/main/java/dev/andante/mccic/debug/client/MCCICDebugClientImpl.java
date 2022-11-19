package dev.andante.mccic.debug.client;

import com.mojang.brigadier.CommandDispatcher;
import dev.andante.mccic.api.client.UnicodeIconsStore;
import dev.andante.mccic.api.client.event.MCCIChatEvent;
import dev.andante.mccic.api.client.event.MCCISoundPlayEvent;
import dev.andante.mccic.api.client.tracker.GameTracker;
import dev.andante.mccic.api.client.tracker.QueueTracker;
import dev.andante.mccic.api.client.util.ClientHelper;
import dev.andante.mccic.api.event.EventResult;
import dev.andante.mccic.api.game.Game;
import dev.andante.mccic.config.client.ClientConfigRegistry;
import dev.andante.mccic.debug.MCCICDebug;
import dev.andante.mccic.debug.client.config.DebugClientConfig;
import dev.andante.mccic.debug.client.config.DebugConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.OptionalInt;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

@Environment(EnvType.CLIENT)
public final class MCCICDebugClientImpl implements MCCICDebug, ClientModInitializer {
    private int y;

    @Override
    public void onInitializeClient() {
        ClientConfigRegistry.INSTANCE.registerAndLoad(DebugClientConfig.CONFIG_HOLDER, DebugConfigScreen::new);
        HudRenderCallback.EVENT.register(this::onHudRender);
        MCCIChatEvent.EVENT.register(this::onChatMessage);
        MCCISoundPlayEvent.EVENT.register(this::onSoundPlay);
        ClientCommandRegistrationCallback.EVENT.register(this::registerCommands);
    }

    private void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(literal(MOD_ID + ":chatrawactionbar").executes(context -> {
            Text text = ClientHelper.getActionBarText();
            if (text != null) {
                String str = ClientHelper.getActionBarText().toString();
                context.getSource().sendFeedback(Text.literal(StringUtils.truncate(str, 200) + "...").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, str)).withUnderline(true)));
                return 1;
            }

            throw new IllegalStateException("No action bar present");
        }));

        dispatcher.register(literal(MOD_ID + ":chatunicodes").executes(context -> {
            UnicodeIconsStore.INSTANCE.getData().ifPresent(data -> {
                MinecraftClient client = MinecraftClient.getInstance();
                ClientPlayerEntity player = client.player;
                for (UnicodeIconsStore.CharPair pair : data.chars()) {
                    player.sendMessage(Text.literal(pair.key() + ": ").append(Text.literal("" + pair.cha()).setStyle(Style.EMPTY.withFont(new Identifier("mcc", "icon")))));
                }
            });
            return 1;
        }));
    }

    private void onHudRender(MatrixStack matrices, float tickDelta) {
        if (!DebugClientConfig.getConfig().debugHud()) {
            return;
        }

        GameTracker gameTracker = GameTracker.INSTANCE;
        if (!gameTracker.isOnServer()) {
            return;
        }

        ClientHelper.drawOpaqueBlack(2, 2, 130, this.y + MinecraftClient.getInstance().textRenderer.fontHeight);
        this.y = 4;

        this.drawText(matrices, Text.of("State: " + gameTracker.getGameState().name()), false);
        this.renderContent(matrices, gameTracker.getGame(), gameTracker.getTime());

        QueueTracker queueTracker = QueueTracker.INSTANCE;
        this.y += 5;
        this.drawText(matrices, Text.literal("                               ").formatted(Formatting.STRIKETHROUGH), false);
        this.y -= 4;
        this.drawText(matrices, Text.of("Queue Type: " + queueTracker.getQueueType().name()));
        this.renderContent(matrices, queueTracker.getGame(), queueTracker.getTime());
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private void renderContent(MatrixStack matrices, Optional<Game> maybeGame, OptionalInt maybeTime) {
        maybeGame.map(Game::getDisplayString).ifPresent(game -> this.drawText(matrices, Text.of("Game: " + game)));
        maybeTime.ifPresent(time -> this.drawText(matrices, Text.of("Time: %s (%sm %ss)".formatted(time, time / 60, time % 60))));
    }

    private void drawText(MatrixStack matrices, Text text, boolean bump) {
        if (bump) {
            this.y += 10;
        }

        MinecraftClient.getInstance().textRenderer.draw(matrices, text, 4, this.y, 0xFFFFFF);
    }

    private void drawText(MatrixStack matrices, Text text) {
        this.drawText(matrices, text, true);
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

    private void onSoundPlay(MCCISoundPlayEvent.Context context) {
        if (DebugClientConfig.getConfig().chatAllSounds()) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null) {
                SoundInstance sound = context.soundInstance();
                client.player.sendMessage(Text.of("%s - %s".formatted(sound.getId(), sound.getSound().getIdentifier())));
            }
        }
    }
}

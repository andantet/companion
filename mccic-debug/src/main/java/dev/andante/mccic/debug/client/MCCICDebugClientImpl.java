package dev.andante.mccic.debug.client;

import com.mojang.brigadier.CommandDispatcher;
import dev.andante.mccic.api.client.UnicodeIconsStore;
import dev.andante.mccic.api.client.event.MCCIChatEvent;
import dev.andante.mccic.api.client.event.MCCISoundPlayEvent;
import dev.andante.mccic.api.client.tracker.GameTracker;
import dev.andante.mccic.api.client.tracker.PartyTracker;
import dev.andante.mccic.api.client.tracker.PartyTracker.PartyMember;
import dev.andante.mccic.api.client.tracker.QueueTracker;
import dev.andante.mccic.api.client.util.ClientHelper;
import dev.andante.mccic.api.event.EventResult;
import dev.andante.mccic.api.game.Game;
import dev.andante.mccic.config.client.ClientConfigRegistry;
import dev.andante.mccic.config.client.command.MCCICConfigCommand;
import dev.andante.mccic.debug.MCCICDebug;
import dev.andante.mccic.debug.client.config.DebugClientConfig;
import dev.andante.mccic.debug.client.config.DebugConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

@Environment(EnvType.CLIENT)
public final class MCCICDebugClientImpl implements MCCICDebug, ClientModInitializer {
    private int y;

    @Override
    public void onInitializeClient() {
        ClientConfigRegistry.INSTANCE.registerAndLoad(DebugClientConfig.CONFIG_HOLDER, DebugConfigScreen::new);

        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            MCCICConfigCommand.registerNewConfig(ID, DebugConfigScreen::new);
        }

        HudRenderCallback.EVENT.register(this::onHudRender);
        MCCIChatEvent.EVENT.register(this::onChatMessage);
        MCCISoundPlayEvent.EVENT.register(this::onSoundPlay);
        ClientCommandRegistrationCallback.EVENT.register(this::registerCommands);
    }

    private void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(literal(MOD_ID + ":chat_raw_action_bar").executes(context -> {
            Text text = ClientHelper.getActionBarText();
            if (text instanceof MutableText mutable) {
                String str = text.toString();
                context.getSource().sendFeedback(mutable.copy().setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, str)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(StringUtils.truncate(str, 100) + "...").formatted(Formatting.UNDERLINE)))));
                return 1;
            }

            throw new IllegalStateException("No action bar present");
        }));

        dispatcher.register(literal(MOD_ID + ":chat_unicodes").executes(context -> {
            UnicodeIconsStore.INSTANCE.getData().ifPresent(data -> {
                for (UnicodeIconsStore.Icon icon : UnicodeIconsStore.Icon.values()) {
                    String key = icon.getKey();
                    char cha = data.getCharacterFor(key);
                    context.getSource().sendFeedback(Text.literal(key + ": ").append(Text.literal(String.valueOf(cha)).setStyle(Style.EMPTY.withFont(icon.getFont().getFont()))));
                }
            });
            return 1;
        }));

        dispatcher.register(literal(MOD_ID + ":chat_sidebar_names").executes(context -> {
            Scoreboard scoreboard = ClientHelper.getScoreboard().orElse(null);
            for (String name : ClientHelper.getScoreboardPlayerNames().orElse(Collections.emptyList())) {
                Team team = scoreboard.getPlayerTeam(name);
                MutableText text = team == null ? Text.literal(name) : team.decorateName(Text.literal(name));
                String str = text.toString();
                context.getSource().sendFeedback(text.setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, str)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(StringUtils.truncate(str, 100) + "...").formatted(Formatting.UNDERLINE)))));
            }
            return 1;
        }));

        dispatcher.register(literal(MOD_ID + ":chat_party_instance").executes(context -> {
            List<PartyMember> members = PartyTracker.INSTANCE.getMembers();
            for (PartyMember member : members) {
                context.getSource().sendFeedback(Text.of("- " + member.name() + ", " + member.status()));
            }
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
        player.sendMessage(Text.literal(String.valueOf(message.getContent().getClass())), true);
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

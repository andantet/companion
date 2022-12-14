package dev.andante.mccic.chat.client;

import dev.andante.mccic.api.client.UnicodeIconsStore;
import dev.andante.mccic.api.client.UnicodeIconsStore.Icon;
import dev.andante.mccic.api.client.event.MCCIChatEvent;
import dev.andante.mccic.api.client.tracker.GameTracker;
import dev.andante.mccic.api.event.EventResult;
import dev.andante.mccic.api.game.Game;
import dev.andante.mccic.api.game.Games;
import dev.andante.mccic.chat.MCCICChat;
import dev.andante.mccic.chat.client.config.ChatClientConfig;
import dev.andante.mccic.chat.client.config.ChatConfigScreen;
import dev.andante.mccic.chat.mixin.MutableTextAccessor;
import dev.andante.mccic.config.client.ClientConfigRegistry;
import dev.andante.mccic.config.client.command.MCCICConfigCommand;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.*;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Environment(EnvType.CLIENT)
public final class MCCICChatClientImpl implements MCCICChat, ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientConfigRegistry.INSTANCE.registerAndLoad(ChatClientConfig.CONFIG_HOLDER, ChatConfigScreen::new);
        MCCICConfigCommand.registerNewConfig(ID, ChatConfigScreen::new);
        MCCIChatEvent.EVENT.register(this::onChat);
    }

    private EventResult onChat(MCCIChatEvent.Context context) {
        GameTracker gameTracker = GameTracker.INSTANCE;
        Optional<Game> maybeGame = gameTracker.getGame();
        if (maybeGame.isPresent()) {
            ChatClientConfig config = ChatClientConfig.getConfig();
            Game game = maybeGame.get();
            if ((game == Games.HOLE_IN_THE_WALL && config.hideHitwDeathMessages()) || (game == Games.TGTTOS && config.hideTgttosDeathMessages())) {
                if (UnicodeIconsStore.isPrefixedWith(Icon.DEATH, context.message())) {
                    return EventResult.cancel();
                }
            }
        }

        return EventResult.pass();
    }

    /**
     * Takes a given text and modifies it such that the regex is replaced and highlighted.
     *
     * <p>
     *     Example:
     *     <pre>{Jeff}[siblings={Bill, Grant}]}, 'Jeff', 'Jeff'</pre>
     *     becomes
     *     <pre>{}[siblings={*Jeff*, Bill, Grant}]</pre>
     * </p>
     *
     * @param pattern must contain at least 3 captured groups - 1: before, 2: replacement, 3: after
     */
    public static Text replaceAndHighlightRegex(Text text, Pattern pattern, int color) {
        if (text instanceof MutableText mutable && text.getContent() instanceof LiteralTextContent content) {
            String raw = content.string();
            Matcher matcher = pattern.matcher(raw);
            if (matcher.matches()) {
                ((MutableTextAccessor) mutable).setContent(TextContent.EMPTY);

                String before = matcher.group(1);
                String replacement = matcher.group(2);
                String after = matcher.group(3);

                MutableText modified = Text.empty();
                modified.append(replaceAndHighlightRegex(Text.literal(before), pattern, color));
                modified.append(Text.literal(replacement).fillStyle(Style.EMPTY.withColor(color)));
                modified.append(replaceAndHighlightRegex(Text.literal(after), pattern, color));
                mutable.getSiblings().add(0, modified);
            }
        }

        return text;
    }
}

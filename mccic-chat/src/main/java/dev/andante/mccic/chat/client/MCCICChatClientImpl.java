package dev.andante.mccic.chat.client;

import com.mojang.authlib.GameProfile;
import dev.andante.mccic.api.client.event.MCCIChatEvent;
import dev.andante.mccic.api.event.EventResult;
import dev.andante.mccic.api.util.TextQuery;
import dev.andante.mccic.chat.MCCICChat;
import dev.andante.mccic.chat.client.config.ChatClientConfig;
import dev.andante.mccic.chat.client.config.ChatConfigScreen;
import dev.andante.mccic.chat.mixin.MutableTextAccessor;
import dev.andante.mccic.config.client.ClientConfigRegistry;
import dev.andante.mccic.config.client.command.MCCICConfigCommand;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import net.minecraft.text.*;

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
        ChatClientConfig config = ChatClientConfig.getConfig();

        if (config.mentions()) {
            Text message = context.message();
            MinecraftClient client = MinecraftClient.getInstance();
            Session session = client.getSession();
            GameProfile profile = session.getProfile();
            String profileName = profile.getName();

            String regex = "(.*)" + profileName + "(.*)";
            TextQuery.findTexts(message, text -> {
                if (text.getContent() instanceof LiteralTextContent content) {
                    String raw = content.string();
                    return raw.matches(regex);
                }

                return false;
            }).forEach(query -> replaceAndHighlightRegex(query.getResult(), regex, profileName, config.mentionsColor()));
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
     * @param regex must contain at least 2 captured groups
     */
    public static Text replaceAndHighlightRegex(Text text, String regex, String replacement, int color) {
        if (text instanceof MutableText mutable && text.getContent() instanceof LiteralTextContent content) {
            String raw = content.string();
            Matcher matcher = Pattern.compile(regex).matcher(raw);
            if (matcher.matches()) {
                ((MutableTextAccessor) mutable).setContent(TextContent.EMPTY);

                String before = matcher.group(1);
                String after = matcher.group(2);

                MutableText modified = Text.empty();
                modified.append(replaceAndHighlightRegex(Text.literal(before), regex, replacement, color));
                modified.append(Text.literal(replacement).fillStyle(Style.EMPTY.withColor(color)));
                modified.append(replaceAndHighlightRegex(Text.literal(after), regex, replacement, color));
                mutable.getSiblings().add(0, modified);
            }
        }

        return text;
    }
}

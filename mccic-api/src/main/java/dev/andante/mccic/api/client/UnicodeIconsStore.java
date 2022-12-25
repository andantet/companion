package dev.andante.mccic.api.client;

import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.ListCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.andante.mccic.api.MCCIC;
import dev.andante.mccic.api.client.event.ClientLoginSuccessEvent;
import dev.andante.mccic.api.util.JsonHelper;
import dev.andante.mccic.api.util.TextQuery;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.Text;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class UnicodeIconsStore {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final UnicodeIconsStore INSTANCE = new UnicodeIconsStore("https://gist.githubusercontent.com/andantet/702a32539377b363bc6ae0c09d2982d2/raw/unicode_icons.json");

    private final URL url;
    private Data data;

    public UnicodeIconsStore(String url) {
        try {
            this.url = new URL(url);
        } catch (MalformedURLException exception) {
            throw new RuntimeException(exception);
        }

        ClientLoginSuccessEvent.EVENT.register(this::onClientLogin);
    }

    public static boolean doesTextContainIcon(Text text, Icon icon) {
        return TextQuery.findText(text, ".*" + INSTANCE.getCharacterFor(icon) + ".*").isPresent();
    }

    public static boolean doesTextContainIconExact(Text text, Icon icon) {
        return TextQuery.findText(text, "" + INSTANCE.getCharacterFor(icon)).isPresent();
    }

    public static boolean isPrefixedWith(Icon icon, Text text) {
        try {
            Text iconText = text.getSiblings().get(0).getSiblings().get(0);
            if (iconText.getContent() instanceof LiteralTextContent content) {
                return content.string().equals("" + UnicodeIconsStore.INSTANCE.getCharacterFor(icon));
            }
        } catch (IndexOutOfBoundsException ignored) {
        }

        return false;
    }

    protected void onClientLogin(ClientLoginNetworkHandler handler, LoginSuccessS2CPacket packet) {
        this.retrieve();
    }

    /**
     * Updates {@link #data} based on the data given from the defined {@link #url}.
     */
    public void retrieve() {
        try {
            JsonReader reader = new JsonReader(new InputStreamReader(url.openStream()));
            Optional<Data> maybeData = Data.CODEC.parse(JsonOps.INSTANCE, JsonHelper.parseJsonReader(reader)).result();
            this.data = maybeData.orElseThrow(() -> new IOException("Codec could not parse data from unicode icons source file"));
        } catch (IOException | JsonSyntaxException | IllegalStateException exception) {
            LOGGER.error("%s: Retrieval Error".formatted(MCCIC.MOD_NAME), exception);
        }
    }

    public URL getUrl() {
        return this.url;
    }

    public Optional<Data> getData() {
        return Optional.ofNullable(this.data);
    }

    public char getCharacterFor(String key) {
        return this.getData().map(datax -> datax.getCharacterFor(key)).orElse('\u0000');
    }

    public char getCharacterFor(Icon icon) {
        return this.getCharacterFor(icon.getKey());
    }

    public record Data(int schemaVersion, List<CharPair> chars) {
        public static final Codec<Data> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                Codec.INT.fieldOf("schema_version")
                         .forGetter(Data::schemaVersion),
                new ListCodec<>(CharPair.CODEC).fieldOf("chars")
                                               .forGetter(Data::chars)
            ).apply(instance, Data::new)
        );

        public char getCharacterFor(String key) {
            return this.chars.stream()
                             .filter(pair -> Objects.equals(pair.key, key))
                             .findFirst()
                             .map(CharPair::cha)
                             .orElse('\u0000');
        }
    }

    public record CharPair(String key, Character cha) {
        public static final Codec<CharPair> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                Codec.STRING.fieldOf("key")
                    .forGetter(CharPair::key),
                Codec.STRING.fieldOf("char")
                    .xmap(s -> s.toCharArray()[0], Object::toString)
                    .forGetter(CharPair::cha)
            ).apply(instance, CharPair::new)
        );
    }

    public enum Icon {
        DEATH("death"),
        QUEST_BOOK("quest_book"),
        CHAT_LOCAL("chat_local"),
        CHAT_PARTY("chat_party"),
        CHAT_TEAM("chat_team"),
        CROWN("crown"),
        FADE("fade"),
        GUI_WARDROBE("gui_wardrobe"),
        GUI_WARDROBE_EDITOR("gui_wardrobe_editor"),
        GUI_WARDROBE_EDITOR_FULL("gui_wardrobe_editor_full"),
        GUI_WARDROBE_EDITOR_COLORS_SWATCH("gui_wardrobe_editor_colors_swatch"),
        GUI_WARDROBE_EDITOR_FULL_COLORS_FACTION("gui_wardrobe_editor_full_colors_faction"),
        GUI_WARDROBE_EDITOR_COLORS_FACTION("gui_wardrobe_editor_colors_faction"),
        GUI_WARDROBE_EDITOR_FULL_VARIANTS("gui_wardrobe_editor_full_variants"),
        GUI_WARDROBE_EDITOR_VARIANTS("gui_wardrobe_editor_variants"),
        GUI_BETA_TEST_WARNING("gui_beta_test_warning");

        private final String key;

        Icon(String key) {
            this.key = key;
        }

        public String getKey() {
            return this.key;
        }
    }
}

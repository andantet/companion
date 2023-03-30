package dev.andante.mccic.api.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LazilyParsedNumber;
import com.google.gson.stream.JsonReader;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import dev.andante.mccic.api.MCCIC;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Optional;
import java.util.function.UnaryOperator;
import org.slf4j.Logger;

public interface JsonHelper {
    Logger LOGGER = LogUtils.getLogger();

    static JsonElement parseJsonReader(JsonReader reader) throws IOException {
        return switch (reader.peek()) {
            case STRING -> new JsonPrimitive(reader.nextString());
            case NUMBER -> new JsonPrimitive(new LazilyParsedNumber(reader.nextString()));
            case BOOLEAN -> new JsonPrimitive(reader.nextBoolean());
            case NULL -> {
                reader.nextNull();
                yield JsonNull.INSTANCE;
            }
            case BEGIN_ARRAY -> {
                JsonArray array = new JsonArray();
                reader.beginArray();
                while (reader.hasNext()) {
                    array.add(parseJsonReader(reader));
                }
                reader.endArray();
                yield array;
            }
            case BEGIN_OBJECT -> {
                JsonObject object = new JsonObject();
                reader.beginObject();
                while (reader.hasNext()) {
                    object.add(reader.nextName(), parseJsonReader(reader));
                }
                reader.endObject();
                yield object;
            }
            default -> throw new IllegalArgumentException();
        };
    }

    static <T> Optional<T> parseCodecUrl(URL url, Codec<T> codec, UnaryOperator<JsonElement> modifier) {
        try {
            JsonReader reader = new JsonReader(new InputStreamReader(url.openStream()));
            return codec.parse(JsonOps.INSTANCE, modifier.apply(JsonHelper.parseJsonReader(reader))).result();
        } catch (IOException | JsonSyntaxException | IllegalStateException exception) {
            LOGGER.error("%s: Retrieval Error".formatted(MCCIC.MOD_NAME), exception);
        }

        return Optional.empty();
    }

    static <T> Optional<T> parseCodecUrl(URL url, Codec<T> codec) {
        return parseCodecUrl(url, codec, UnaryOperator.identity());
    }
}

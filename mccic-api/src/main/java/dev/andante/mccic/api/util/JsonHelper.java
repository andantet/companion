package dev.andante.mccic.api.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.LazilyParsedNumber;
import com.google.gson.stream.JsonReader;

import java.io.IOException;

public interface JsonHelper {
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
}

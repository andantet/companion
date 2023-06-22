package dev.andante.companion.api.serialization

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import dev.andante.companion.api.extension.asyncApplyOnCompletion
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.net.URL

/**
 * Fetches a string from a url and parses it as a JSON object.
 */
class AsyncJsonFetcher(
    /**
     * The url.
     */
    url: String
) {
    private val url = URL(url)

    /**
     * Fetches the json asynchronously.
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun fetch(): Deferred<JsonElement> {
        return GlobalScope.async {
            val jsonText = url.readText()
            return@async JsonParser.parseString(jsonText)
        }
    }

    /**
     * Fetches the json asynchronously and runs the given [action] on completion.
     */
    fun fetchAndRun(action: (JsonElement) -> Unit): Deferred<JsonElement> {
        return fetch().asyncApplyOnCompletion { jsonElement ->
            action(jsonElement)
            jsonElement
        }
    }

    /**
     * Asynchronously fetches the json and decodes the result according
     * to the given [codec].
     */
    fun <T> fetchAndParseCodec(codec: Codec<T>): Deferred<T> {
        return fetch().asyncApplyOnCompletion { jsonElement ->
            val decoded = codec.decode(JsonOps.INSTANCE, jsonElement)
            return@asyncApplyOnCompletion decoded.result().orElseThrow().first
        }
    }

    /**
     * Asynchronously fetches the json and decodes the result into
     * a map with strings as the key, based on the [valueCodec].
     */
    fun <T> fetchStringKeyMap(valueCodec: Codec<T>): Deferred<Map<String, T>> {
        return fetch().asyncApplyOnCompletion { jsonElement ->
            if (jsonElement !is JsonObject) {
                throw JsonParseException("Retrieved json was not a json object")
            }

            return@asyncApplyOnCompletion jsonElement.entrySet().map { (key, value) ->
                val decoded = valueCodec.decode(JsonOps.INSTANCE, value)
                return@map key to decoded.result().orElseThrow().first
            }.toMap()
        }
    }
}

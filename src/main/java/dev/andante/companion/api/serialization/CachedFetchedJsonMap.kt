package dev.andante.companion.api.serialization

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import dev.andante.companion.api.extension.asyncApplyOnCompletion
import dev.andante.companion.api.helper.FileHelper.companionFile
import kotlinx.coroutines.Deferred
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.FileNotFoundException

/**
 * A json map, fetched from a url, cached for when the remote map cannot be reached.
 */
open class CachedFetchedJsonMap<T>(
    /**
     * The id of the cache file.
     */
    val cacheId: String,

    /**
     * The url to fetch from.
     */
    url: String,

    /**
     * The codec to parse the map value.
     */
    private val codec: Codec<T>
) {
    /**
     * The fetcher for the remotely-hosted json.
     */
    private val fetcher = AsyncJsonFetcher(url)

    /**
     * The map fetched from the url.
     */
    private val fetchedMap = mutableMapOf<String, T>()

    /**
     * The file to serialize cached maps to/from.
     */
    private val cacheFile = companionFile(".cache/fetched_json_maps/$cacheId.json")

    /**
     * The cached map stored on disk. Used when the map cannot be fetched.
     */
    private val cachedMap = mutableMapOf<String, T>()

    init {
        // retrieve cache from file
        try {
            val cachedJsonString = cacheFile.readText()
            val json = JsonParser.parseString(cachedJsonString)
            if (json is JsonObject) {
                json.entrySet().forEach { (key, value) ->
                    val decoded = codec.decode(JsonOps.INSTANCE, value)
                    cachedMap[key] = decoded.result().orElseThrow().first
                }
            }
        } catch (_: FileNotFoundException) {
        } catch (exception: Exception) {
            LOGGER.error("Failed to load fetched cache", exception)
        }
    }

    /**
     * Fetches the map from the url and stores the acquired map.
     */
    fun fetch(): Deferred<Map<String, T>> {
        val deferred = fetcher.fetchStringKeyMap(codec)

        deferred.asyncApplyOnCompletion { map ->
            // cache the result
            val json = JsonObject()
            map.forEach { (key, value) ->
                val encoded = codec.encodeStart(JsonOps.INSTANCE, value)
                json.add(key, encoded.result().orElseThrow())
            }

            val gson = GsonBuilder().create()
            cacheFile.parentFile.mkdirs()
            cacheFile.writeText(gson.toJson(json))

            synchronized(cachedMap) {
                cachedMap.clear()
                cachedMap.putAll(map)
            }

            // store the result to memory
            synchronized(fetchedMap) {
                fetchedMap.clear()
                fetchedMap.putAll(map)
            }
        }.invokeOnCompletion { throwable ->
            if (throwable != null) {
                LOGGER.error("Could not fetch map", throwable)
            }
        }

        return deferred
    }

    /**
     * Retrieves a key from the map.
     */
    @Throws(IllegalArgumentException::class)
    operator fun get(key: String): T? {
        return fetchedMap[key] ?: cachedMap[key]
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger("Remote Cache")
    }
}

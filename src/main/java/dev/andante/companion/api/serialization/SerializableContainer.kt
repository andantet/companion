package dev.andante.companion.api.serialization

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

/**
 * Contains an object that can be serialized.
 */
open class SerializableContainer<T>(
    /**
     * The id of this container.
     */
    val id: String,

    /**
     * The serialization codec for the object.
     */
    private val codec: Codec<T>,

    /**
     * The default object.
     */
    default: T,

    /**
     * The file to save to/load from.
     */
    private val file: File,

    /**
     * The file to back-up to.
     */
    private val backupFile: File
) {
    /**
     * The object's backing variable.
     */
    private var _serializableObject: T = default

    /**
     * The object.
     */
    val serializableObject: T get() = _serializableObject

    /**
     * Saves the current object to disk.
     */
    fun save() {
        try {
            // encode
            val encodedSettings = codec.encodeStart(JsonOps.INSTANCE, serializableObject)
                .result()
                .orElseThrow()

            // write to file
            val gson = GsonBuilder().setPrettyPrinting().create()
            val json = gson.toJson(encodedSettings)
            file.parentFile.mkdirs()
            file.writeText(json)
        } catch (throwable: Throwable) {
            LOGGER.error("Something went wrong saving serializable object: $id", throwable)
        }
    }

    /**
     * Loads the object from disk.
     */
    fun load() : T {
        if (!file.exists()) {
            save()
            return serializableObject
        }

        try {
            // decode
            val json = JsonParser.parseReader(file.reader())
            val decodedSettings = codec.decode(JsonOps.INSTANCE, json)
                .map { it.first }
                .result()
                .orElseThrow()

            // set
            _serializableObject = decodedSettings

            // resave (in case of updates to schemas)
            save()

            return decodedSettings
        } catch (exception: Exception) {
            LOGGER.error("Something went wrong loading serializable object: $id", exception)

            // try backup
            if (file.exists()) {
                file.copyTo(backupFile, true)
            }

            // resave
            save()

            // try once more
            try {
                return load()
            } catch (exception: Exception) {
                LOGGER.error("Something went fatally wrong", exception)
                throw exception
            }
        }
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger("[MCCI: Companion] Serialization")
    }
}

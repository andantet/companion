package dev.andante.companion.data.sound

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.data.DataOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.DataWriter
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Identifier
import org.jetbrains.annotations.ApiStatus
import java.util.Collections
import java.util.Objects
import java.util.concurrent.CompletableFuture

/**
 * Extend this class and implement [SoundProvider.generateSounds].
 */
abstract class SoundProvider protected constructor(private val dataOutput: FabricDataOutput) : DataProvider {
    /**
     * Implement this method to register sounds.
     *
     *
     * Call [SoundGenerator.add] to add a list of sound entries
     * for a given [SoundEvent].
     */
    abstract fun generateSounds(soundGenerator: SoundGenerator)

    override fun run(writer: DataWriter): CompletableFuture<*> {
        val soundEvents = mutableMapOf<String, JsonObject>()
        generateSounds { sound, replace, subtitle, entries ->
            Objects.requireNonNull(sound)
            Objects.requireNonNull(entries)

            val keys = entries.map(SoundBuilder::name)
            if (keys.any { id -> Collections.frequency(keys, id) > 1 }) {
                throw RuntimeException("Entries for sound event " + sound.id + " contain duplicate sound names. Event will be omitted.")
            }

            val soundEventData = JsonObject()
            val soundEntries = JsonArray()

            entries.forEach { builder -> soundEntries.add(builder.build()) }
            soundEventData.add("sounds", soundEntries)

            if (replace) {
                soundEventData.addProperty("replace", true)
            }

            if (subtitle != null) {
                soundEventData.addProperty("subtitle", subtitle)
            }

            soundEvents[sound.id.path] = soundEventData
        }

        val soundsJson = JsonObject()
        soundEvents.forEach(soundsJson::add)

        val soundsPath = dataOutput
            .getResolver(DataOutput.OutputType.RESOURCE_PACK, ".")
            .resolveJson(Identifier(dataOutput.modId, "sounds"))
        return DataProvider.writeToPath(writer, soundsJson, soundsPath.normalize())
    }

    override fun getName(): String {
        return "Sounds"
    }

    @ApiStatus.NonExtendable
    fun interface SoundGenerator {
        /**
         * Adds an individual [SoundEvent] and its respective sounds to your mod's `sounds.json` file.
         *
         * @param sound The [SoundEvent] to add an entry for.
         * @param replace Set this to `true` if this entry corresponds to a sound event from vanilla
         * Minecraft or some other mod's namespace, in order to replace the default sounds from the
         * original namespace's sounds file via your own namespace's resource pack.
         * @param subtitle An optional subtitle to use for the event, given as a translation key for the subtitle.
         * @param sounds A list of [SoundBuilder] instances from which to generate individual sound entry data for
         * this event.
         */
        fun add(sound: SoundEvent, replace: Boolean, subtitle: String?, vararg sounds: SoundBuilder)

        /**
         * Adds an individual [SoundEvent] and its respective sounds to your mod's `sounds.json` file.
         *
         * @param sound The [SoundEvent] to add an entry for.
         * @param replace Set this to `true` if this entry corresponds to a sound event from vanilla
         * Minecraft or some other mod's namespace, in order to replace the default sounds from the
         * original namespace's sounds file via your own namespace's resource pack.
         * @param sounds A list of [SoundBuilder] instances from which to generate individual sound entry data for
         * this event.
         */
        fun add(sound: SoundEvent, replace: Boolean, vararg sounds: SoundBuilder) {
            add(sound, replace, null, *sounds)
        }

        /**
         * Adds an individual [SoundEvent] and its respective sounds to your mod's `sounds.json` file.
         *
         * @param sound The [SoundEvent] to add an entry for.
         * @param subtitle An optional subtitle to use for the event, given as a translation key for the subtitle.
         * @param sounds A list of [SoundBuilder] instances from which to generate individual sound entry data for
         * this event.
         */
        fun add(sound: SoundEvent, subtitle: String?, vararg sounds: SoundBuilder) {
            add(sound, false, subtitle, *sounds)
        }

        /**
         * Adds an individual [SoundEvent] and its respective sounds to your mod's `sounds.json` file.
         *
         * @param sound The [SoundEvent] to add an entry for.
         * @param sounds A list of [SoundBuilder] instances from which to generate individual sound entry data for
         * this event.
         */
        fun add(sound: SoundEvent, vararg sounds: SoundBuilder) {
            add(sound, false, null, *sounds)
        }

        /**
         * Adds an individual [SoundEvent] and its respective sound to your mod's `sounds.json` file.
         * @param sound The [SoundEvent] to add an entry for.
         */
        fun addDefault(id: Identifier, builder: SoundBuilder.() -> SoundBuilder = { this }) {
            add(SoundEvent.of(id), builder(convertToBuilder(id)))
        }

        private fun convertToBuilder(id: Identifier): SoundBuilder {
            return SoundBuilder.sound(Identifier(id.namespace, id.path.replace('.', '/')))
        }
    }
}

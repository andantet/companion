package dev.andante.companion.data.sound

import com.google.common.base.Preconditions
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import net.minecraft.util.Identifier

/**
 * Utility class for building a sound entry with a given set of properties, without necessarily passing them all as parameters.
 */
class SoundBuilder private constructor(
    val name: Identifier,
    private val event: Boolean
) {
    private var volume = 1f
    private var pitch = 1f
    private var weight = 1
    private var stream = false
    private var attenuationDistance = 16
    private var preload = false

    /**
     * Sets the volume that the sound should play at as a number between `0.0` and `1.0`. Defaults
     * to `1.0`.
     */
    fun setVolume(volume: Float): SoundBuilder {
        Preconditions.checkArgument(volume in 0.0..1.0)
        this.volume = volume
        return this
    }

    /**
     * Sets the pitch that the sound should play at. Note that this is internally clamped in-game between 0.5 and 2.0.
     */
    fun setPitch(pitch: Float): SoundBuilder {
        Preconditions.checkArgument(pitch > 0)
        this.pitch = pitch
        return this
    }

    /**
     * Sets how much likelier it should be for this sound to play. For example, setting this to 2 will mean that this
     * sound is twice as likely to play for this event.
     */
    fun setWeight(weight: Int): SoundBuilder {
        Preconditions.checkArgument(weight >= 0)
        this.weight = weight
        return this
    }

    /**
     * Dictates that this sound should be streamed from its file. Recommended for sounds with a much longer play time
     * than a couple of seconds, such as music tracks, in order to minimise lag. If set, only 4 instances of this sound
     * can play in-game at once.
     */
    fun stream(): SoundBuilder {
        stream = true
        return this
    }

    /**
     * Sets the reduction rate of this sound depending on distance from the source. Defaults to 16.
     */
    fun setAttenuationDistance(attenuationDistance: Int): SoundBuilder {
        Preconditions.checkArgument(attenuationDistance >= 0)
        this.attenuationDistance = attenuationDistance
        return this
    }

    /**
     * Dictates that this sound should be loaded in advance when loading the resource pack containing it rather than
     * when the sound itself plays.
     */
    fun preload(): SoundBuilder {
        preload = true
        return this
    }

    private fun allDefaults(): Boolean {
        return volume == 1f && pitch == 1f && weight == 1 && attenuationDistance == 16 && !stream && !preload && !event
    }

    fun build(): JsonElement {
        return if (allDefaults()) {
            JsonPrimitive(name.toString())
        } else {
            val soundEntry = JsonObject()
            soundEntry.addProperty("name", name.toString())
            if (volume != 1f) {
                soundEntry.addProperty("volume", volume)
            }
            if (pitch != 1f) {
                soundEntry.addProperty("pitch", pitch)
            }
            if (weight != 1) {
                soundEntry.addProperty("weight", weight)
            }
            if (stream) {
                soundEntry.addProperty("stream", true)
            }
            if (attenuationDistance != 16) {
                soundEntry.addProperty("attenuation_distance", attenuationDistance)
            }
            if (preload) {
                soundEntry.addProperty("preload", true)
            }
            if (event) {
                soundEntry.addProperty("type", "event")
            }
            soundEntry
        }
    }

    companion object {
        /**
         * Build an entry corresponding to a sound file.
         *
         * @param name The name of the sound as a namespaced ID with relative folder path.
         */
        fun sound(name: Identifier): SoundBuilder {
            return SoundBuilder(name, false)
        }

        /**
         * Build an entry corresponding to an existing [net.minecraft.sound.SoundEvent].
         *
         * @param name The ID of the sound event.
         */
        fun event(name: Identifier): SoundBuilder {
            return SoundBuilder(name, true)
        }
    }
}

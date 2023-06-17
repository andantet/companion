package dev.andante.companion.api.sound

import dev.andante.companion.api.event.SoundPlayCallback
import dev.andante.companion.api.sound.instance.GameMusicSoundInstance
import dev.andante.companion.api.sound.instance.SimpleSoundInstance
import net.minecraft.client.MinecraftClient
import net.minecraft.client.sound.SoundInstance
import net.minecraft.sound.SoundCategory
import net.minecraft.util.Identifier

/**
 * Manages client music.
 */
object CompanionSoundManager {
    private val CLIENT = MinecraftClient.getInstance()

    /**
     * The last played music sound instance.
     */
    private var musicSoundInstance: GameMusicSoundInstance? = null

    init {
        SoundPlayCallback.EVENT.register(::onPlaySound)
    }

    /**
     * Plays the given sound event.
     */
    fun play(id: Identifier) {
        val instance = SimpleSoundInstance(id) { 1.0f }
        CLIENT.soundManager.play(instance)
    }

    /**
     * Plays the given sound event as music.
     */
    fun playMusic(id: Identifier?) {
        musicSoundInstance = if (id == null) {
            // stop previous sound instance
            CLIENT.soundManager.stop(musicSoundInstance)
            null
        } else {
            // play sound
            val newSoundInstance = GameMusicSoundInstance(id) { 1.0f }
            CLIENT.soundManager.play(newSoundInstance)
            newSoundInstance
        }
    }

    private fun onPlaySound(soundInstance: SoundInstance) {
        when (val soundId = soundInstance.sound.identifier) {
            // overtime
            CompanionSounds.MUSIC_OVERTIME_INTRO, CompanionSounds.MUSIC_OVERTIME_LOOP -> {
                stop(soundId)
                playMusic(soundId)
            }
        }
    }

    /**
     * Stops the given sound event.
     */
    fun stop(id: Identifier) {
        CLIENT.soundManager.stopSounds(id, SoundCategory.MASTER)
    }
}

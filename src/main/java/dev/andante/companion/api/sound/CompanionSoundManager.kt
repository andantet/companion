package dev.andante.companion.api.sound

import dev.andante.companion.api.event.OnStopAllSoundsCallback
import dev.andante.companion.api.event.SoundPlayCallback
import dev.andante.companion.api.sound.instance.SimpleSoundInstance
import dev.andante.companion.setting.MusicSettings
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
    private var musicSoundInstance: SimpleSoundInstance? = null

    init {
        SoundPlayCallback.EVENT.register(::onPlaySound)
        OnStopAllSoundsCallback.EVENT.register(::onStopAllSounds)
    }

    /**
     * Plays the given sound event.
     */
    fun play(id: Identifier, volumeSupplier: () -> Float = { 1.0f }) {
        val instance = SimpleSoundInstance(id, false, volumeSupplier)
        CLIENT.soundManager.play(instance)
    }

    /**
     * Plays the given sound event as music.
     */
    fun playMusic(id: Identifier?, repeat: Boolean = true, stop: Boolean = true): SimpleSoundInstance? {
        if (musicSoundInstance?.id == id) {
            return musicSoundInstance
        }

        if (stop) {
            // stop previous sound instance
            musicSoundInstance?.let(CLIENT.soundManager::stop)
        }

        // start new sound instance
        musicSoundInstance = if (id == null) {
            null
        } else {
            // play sound
            val newSoundInstance = SimpleSoundInstance(id, repeat) { MusicSettings.INSTANCE.musicVolume }
            CLIENT.soundManager.play(newSoundInstance)

            if (stop) {
                newSoundInstance
            } else {
                musicSoundInstance
            }
        }

        return musicSoundInstance
    }

    private fun onPlaySound(soundInstance: SoundInstance) {
        if (soundInstance is SimpleSoundInstance) {
            return
        }

        when (val soundId = soundInstance.sound.identifier) {
            // overtime
            CompanionSounds.MUSIC_OVERTIME_INTRO, CompanionSounds.MUSIC_GAMEINTRO, CompanionSounds.MUSIC_ROUNDENDMUSIC -> {
                stop(soundId)
                playMusic(soundId, false, stop = false)
            }

            CompanionSounds.MUSIC_OVERTIME_LOOP -> {
                stop(soundId)
                playMusic(soundId)
            }
        }
    }

    private fun onStopAllSounds() {
        musicSoundInstance = null
    }

    /**
     * Stops the given sound event.
     */
    fun stop(id: Identifier) {
        CLIENT.soundManager.stopSounds(id, SoundCategory.MASTER)
    }

    /**
     * Stops the current music.
     */
    fun stopMusic(): Boolean {
        val wasPlaying = musicSoundInstance?.isDone == false
        CLIENT.soundManager.stop(musicSoundInstance)
        musicSoundInstance = null
        return wasPlaying
    }
}

package dev.andante.companion.api.sound.instance

import net.minecraft.util.Identifier

/**
 * A sound instance for game music.
 */
class GameMusicSoundInstance(id: Identifier, volumeSupplier: () -> Float) : SimpleSoundInstance(id, volumeSupplier) {
    init {
        this.repeat = true
    }
}

package dev.andante.companion.api.sound.instance

import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.sound.SoundInstance
import net.minecraft.client.sound.SoundInstance.AttenuationType
import net.minecraft.client.sound.TickableSoundInstance
import net.minecraft.sound.SoundCategory
import net.minecraft.util.Identifier

/**
 * A simple sound instance.
 */
open class SimpleSoundInstance(
    id: Identifier,
    repeat: Boolean,
    val volumeSupplier: () -> Float
) : PositionedSoundInstance(
    id, SoundCategory.MASTER, 1.0f, 1.0f,
    SoundInstance.createRandom(), repeat, 0, AttenuationType.NONE,
    0.0, 0.0, 0.0, true
), TickableSoundInstance {
    override fun tick() {
    }

    override fun isDone(): Boolean {
        return false
    }

    override fun getVolume(): Float {
        return volumeSupplier()
    }
}

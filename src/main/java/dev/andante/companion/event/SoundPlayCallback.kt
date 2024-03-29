package dev.andante.companion.event

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.client.sound.SoundInstance

fun interface SoundPlayCallback {
    fun onSoundPlay(soundInstance: SoundInstance)

    companion object {
        /**
         * An event for when the client plays a sound.
         */
        val EVENT: Event<SoundPlayCallback> = EventFactory.createArrayBacked(SoundPlayCallback::class.java) { listeners -> SoundPlayCallback { sound ->
            listeners.forEach { it.onSoundPlay(sound) }
        } }
    }
}

package dev.andante.companion.event

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory

fun interface OnStopAllSoundsCallback {
    fun onStopAllSounds()

    companion object {
        /**
         * An event for when the client sound system stops all sounds.
         */
        val EVENT: Event<OnStopAllSoundsCallback> = EventFactory.createArrayBacked(
            OnStopAllSoundsCallback::class.java) { listeners -> OnStopAllSoundsCallback {
            listeners.forEach(OnStopAllSoundsCallback::onStopAllSounds)
        } }
    }
}

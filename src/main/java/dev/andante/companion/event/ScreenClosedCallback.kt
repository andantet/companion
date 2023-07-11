package dev.andante.companion.event

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.client.gui.screen.Screen

fun interface ScreenClosedCallback {
    fun onCloseScreen(screen: Screen)

    companion object {
        /**
         * An event for when the client closes a screen entirely.
         */
        val EVENT: Event<ScreenClosedCallback> = EventFactory.createArrayBacked(ScreenClosedCallback::class.java) { listeners -> ScreenClosedCallback { world ->
            listeners.forEach {
                try {
                    it.onCloseScreen(world)
                } catch (throwable: Throwable) {
                    throwable.printStackTrace()
                }
            }
        } }
    }
}

package dev.andante.companion.api.event

import dev.andante.companion.api.event.TitleEvents.TitleEvent
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.text.Text

/**
 * Events related to game titles.
 */
object TitleEvents {
    /**
     * An event for when the client receives a title packet.
     */
    val TITLE = TitleEvent.create()

    /**
     * An event for when the client receives a subtitle packet.
     */
    val SUBTITLE = TitleEvent.create()

    fun interface TitleEvent {
        /**
         * Called when the client receives a title packet.
         */
        fun onTitle(title: Text)

        companion object {
            fun create(): Event<TitleEvent> {
                return EventFactory.createArrayBacked(TitleEvent::class.java) { listeners -> TitleEvent { title ->
                    listeners.forEach { it.onTitle(title) }
                } }
            }
        }
    }
}

package dev.andante.companion.api.event

import dev.andante.companion.api.event.PacketEvents.PacketEvent
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.network.packet.Packet

/**
 * Events related to packets.
 */
object PacketEvents {
    /**
     * An event for when the client receives a packet.
     */
    val OUT = PacketEvent.create()

    fun interface PacketEvent {
        /**
         * Called on a packet.
         */
        fun onPacket(packet: Packet<*>)

        companion object {
            fun create(): Event<PacketEvent> {
                return EventFactory.createArrayBacked(PacketEvent::class.java) { listeners -> PacketEvent { packet ->
                    listeners.forEach { it.onPacket(packet) }
                } }
            }
        }
    }
}

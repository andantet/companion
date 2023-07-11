package dev.andante.companion.event

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.client.world.ClientWorld

fun interface WorldJoinCallback {
    fun onJoinWorld(world: ClientWorld)

    companion object {
        /**
         * An event for when the client joins a world.
         */
        val EVENT: Event<WorldJoinCallback> = EventFactory.createArrayBacked(WorldJoinCallback::class.java) { listeners -> WorldJoinCallback { world ->
            listeners.forEach {
                try {
                    it.onJoinWorld(world)
                } catch (throwable: Throwable) {
                    throwable.printStackTrace()
                }
            }
        } }
    }
}

package dev.andante.companion.api.player.position.serializer

import dev.andante.companion.api.player.position.TemporalPosition
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.world.ClientWorld

/**
 * Manages position recorders.
 */
object PositionRecorderManager {
    /**
     * The active recorders.
     */
    private val recorders = mutableSetOf<PositionRecorder>()

    init {
        // setup events
        ClientTickEvents.END_WORLD_TICK.register(::tick)
    }

    private fun tick(world: ClientWorld) {
        if (recorders.isNotEmpty()) {
            val player = MinecraftClient.getInstance().player ?: return
            val time = world.time

            // process position
            val temporalPosition = TemporalPosition(player.pos, player.yaw, player.pitch, player.headYaw, player.bodyYaw, player.pose, time)
            recorders.forEach { recorder -> recorder.add(temporalPosition) }
        }
    }

    /**
     * Creates and registers a recorder from the given world.
     */
    fun create(world: ClientWorld): PositionRecorder {
        val serializer = PositionRecorder(world.time)
        recorders.add(serializer)
        return serializer
    }

    /**
     * Removes a recorder.
     */
    fun remove(recorder: PositionRecorder) {
        recorders.remove(recorder)
    }

    /**
     * Removes and returns a compiled recorder.
     */
    fun removeAndCompile(recorder: PositionRecorder): PositionTimeline {
        recorders.remove(recorder)
        return recorder.compile()
    }
}

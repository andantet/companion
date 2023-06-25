package dev.andante.companion.api.player.ghost

import dev.andante.companion.api.event.WorldJoinCallback
import dev.andante.companion.api.player.position.serializer.PositionTimeline
import dev.andante.companion.mixin.ghost.WorldRendererMixin
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity

object GhostPlayerManager {
    /**
     * All rendering ghost players.
     */
    private val ghostPlayers = mutableListOf<GhostPlayerEntity>()

    /**
     * The time at the last tick.
     */
    private var lastTickTime: Long = -1L

    init {
        WorldJoinCallback.EVENT.register { _ ->
            lastTickTime = -1L
            clear()
        }

        ClientTickEvents.END_WORLD_TICK.register { tick() }
    }

    /**
     * Runs every world tick.
     */
    private fun tick() {
        ghostPlayers.forEach(GhostPlayerEntity::tickPosition)
    }

    /**
     * Ticks ghost timelines. Can be called safely multiple times per tick. To be called by other classes when required.
     */
    fun tickTimeline(client: MinecraftClient) {
        val time = client.world?.time ?: 0L
        if (time > lastTickTime) {
            ghostPlayers.removeIf(GhostPlayerEntity::tickTimeline)
            lastTickTime = time
        }
    }

    /**
     * Adds a ghost with the given parameters.
     */
    fun add(timeline: PositionTimeline, repeat: Boolean = false): GhostPlayerEntity? {
        val client = MinecraftClient.getInstance()
        client.world?.let { world ->
            val player = GhostPlayerEntity(timeline, repeat, world, client.session.profile)
            ghostPlayers.add(player)
            return player
        }

        return null
    }

    /**
     * Removes a ghost.
     */
    fun remove(timeline: PositionTimeline): Boolean {
        return ghostPlayers.removeIf { it.timeline == timeline }
    }

    /**
     * Clears all ghosts.
     */
    fun clear() {
        ghostPlayers.clear()
    }

    operator fun contains(timeline: PositionTimeline): Boolean {
        return ghostPlayers.any { it.timeline == timeline }
    }

    /**
     * Whether any of the ghosts glow.
     * @see WorldRendererMixin
     */
    fun shouldRenderGlowing(): Boolean {
        return ghostPlayers.any(Entity::isGlowing)
    }

    /**
     * Gets all ghost players.
     */
    fun getPlayers(): List<GhostPlayerEntity> {
        return ghostPlayers
    }

    /**
     * Resets the timeline of all ghost players.
     */
    fun reset() {
        ghostPlayers.forEach(GhostPlayerEntity::resetTimeline)
    }
}

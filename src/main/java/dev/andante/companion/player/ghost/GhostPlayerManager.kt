package dev.andante.companion.player.ghost

import dev.andante.companion.event.WorldJoinCallback
import dev.andante.companion.mixin.ghost.WorldRendererMixin
import dev.andante.companion.player.position.serializer.IdentifiablePositionTimeline
import dev.andante.companion.player.position.serializer.PositionTimeline
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
    fun add(timeline: IdentifiablePositionTimeline, repeat: Boolean = false): GhostPlayerEntity? {
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
    fun remove(timeline: IdentifiablePositionTimeline): Boolean {
        return ghostPlayers.removeIf { it.timelineReference == timeline }
    }

    /**
     * Clears all ghosts.
     */
    fun clear() {
        ghostPlayers.clear()
    }

    /**
     * Removes any ghost player with a timeline id not in the given map.
     */
    fun tryInvalidatePlayers(registeredTimelines: Map<String, PositionTimeline>) {
        val registeredIds = registeredTimelines.keys
        ghostPlayers.removeIf { player -> !registeredIds.contains(player.timelineReference.id) }
    }

    operator fun contains(timeline: IdentifiablePositionTimeline): Boolean {
        return ghostPlayers.any { it.timelineReference == timeline }
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

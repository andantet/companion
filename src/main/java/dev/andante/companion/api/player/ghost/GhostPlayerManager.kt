package dev.andante.companion.api.player.ghost

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

    init {
        // register tick event
        ClientTickEvents.END_WORLD_TICK.register { tick() }
    }

    /**
     * Ticks ghost timelines.
     */
    private fun tick() {
        ghostPlayers.removeIf(GhostPlayerEntity::tickTimeline)
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
    fun remove(player: GhostPlayerEntity) {
        ghostPlayers.remove(player)
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
}

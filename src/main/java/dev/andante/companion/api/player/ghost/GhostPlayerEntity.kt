package dev.andante.companion.api.player.ghost

import com.mojang.authlib.GameProfile
import dev.andante.companion.Companion
import dev.andante.companion.api.player.position.TemporalPosition
import dev.andante.companion.api.player.position.serializer.IdentifiablePositionTimeline
import net.minecraft.client.network.OtherClientPlayerEntity
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text

/**
 * A player displayed as a ghost.
 */
class GhostPlayerEntity(
    /**
     * The timeline to run.
     */
    val timelineReference: IdentifiablePositionTimeline,

    /**
     * Whether to repeat the timeline.
     */
    private val repeatTimeline: Boolean,

    world: ClientWorld,
    profile: GameProfile
) : OtherClientPlayerEntity(world, profile) {
    /**
     * The current tick of the timeline.
     */
    private var timelineTick = 0L

    /**
     * Ticks the position of the player based on [timelineTick].
     */
    fun tickPosition() {
        prevX = x
        prevY = y
        prevZ = z

        lastRenderX = x
        lastRenderY = y
        lastRenderZ = z

        prevYaw = yaw
        prevPitch = pitch

        prevHeadYaw = headYaw
        prevBodyYaw = bodyYaw

        // update positions
        timelineReference.timeline.mappedPositions[timelineTick]?.let(::updateTemporalPosition)

        // animate
        updateLimbs(false)
    }

    /**
     * Ticks the timeline and its relevant information.
     */
    fun tickTimeline(): Boolean {
        // check finished
        val finished = timelineReference.timeline.isFinished(timelineTick)
        return if (finished) {
            if (repeatTimeline) {
                // repeat
                resetTimeline()
                false
            } else {
                // finish
                true
            }
        } else {
            // tick timeline
            timelineTick++
            false
        }
    }

    /**
     * Resets the timeline tick.
     */
    fun resetTimeline() {
        timelineTick = 0
    }

    /**
     * Updates the player's positions with the given temporal position.
     */
    private fun updateTemporalPosition(temporalPosition: TemporalPosition) {
        setPosition(temporalPosition.pos)

        yaw = temporalPosition.yaw
        pitch = temporalPosition.pitch

        headYaw = temporalPosition.headYaw
        bodyYaw = temporalPosition.bodyYaw

        pose = temporalPosition.entityPose
    }

    override fun getDisplayName(): Text {
        return Text.translatable("entity.${Companion.MOD_ID}.ghost")
    }

    override fun getTeamColorValue(): Int {
        return 0xA8A8FC
    }

    override fun isGlowing(): Boolean {
        return true
    }

    override fun shouldRenderName(): Boolean {
        return true
    }

    override fun isInvisible(): Boolean {
        return true
    }

    override fun isInvisibleTo(player: PlayerEntity): Boolean {
        return false
    }
}

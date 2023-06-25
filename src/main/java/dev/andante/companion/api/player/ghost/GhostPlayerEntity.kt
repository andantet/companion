package dev.andante.companion.api.player.ghost

import com.mojang.authlib.GameProfile
import dev.andante.companion.Companion
import dev.andante.companion.api.player.position.serializer.PositionTimeline
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
    private val timeline: PositionTimeline,

    /**
     * Whether to repeat the timeline.
     */
    private val repeatTimeline: Boolean,

    world: ClientWorld,
    profile: GameProfile
) : OtherClientPlayerEntity(world, profile) {
    private var timelineTick = 0L

    fun tickTimeline(): Boolean {
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
        timeline.mappedPositions[timelineTick]?.let { temporalPosition ->
            setPosition(temporalPosition.pos)

            yaw = temporalPosition.yaw
            pitch = temporalPosition.pitch

            headYaw = temporalPosition.headYaw
            bodyYaw = temporalPosition.bodyYaw

            pose = temporalPosition.entityPose
        }

        // animate
        updateLimbs(false)

        // check finished
        val finished = timeline.isFinished(timelineTick)
        return if (finished) {
            if (repeatTimeline) {
                // repeat
                timelineTick = 0
                resetPosition()
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

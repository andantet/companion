package dev.andante.companion.api.player.position

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.entity.EntityPose
import net.minecraft.util.math.Vec3d

/**
 * A position in space and time.
 */
data class TemporalPosition(
    /**
     * The coordinate.
     */
    val pos: Vec3d,

    /**
     * The yaw.
     */
    val yaw: Float,

    /**
     * The pitch.
     */
    val pitch: Float,

    /**
     * The head yaw.
     */
    val headYaw: Float,

    /**
     * The body yaw.
     */
    val bodyYaw: Float,

    /**
     * The pose.
     */
    val entityPose: EntityPose,

    /**
     * The time.
     */
    val time: Long
) {
    /**
     * Whether this position equals another, ignoring time.
     */
    fun equalsIgnoreTime(other: TemporalPosition): Boolean {
        return this == other.copy(time = time)
    }

    companion object {
        /**
         * The codec of a temporal position.
         */
        val CODEC: Codec<TemporalPosition> = RecordCodecBuilder.create { instance ->
            instance.group(
                Vec3d.CODEC.fieldOf("pos").forGetter(TemporalPosition::pos),
                Codec.FLOAT.fieldOf("yaw").forGetter(TemporalPosition::yaw),
                Codec.FLOAT.fieldOf("pitch").forGetter(TemporalPosition::pitch),
                Codec.FLOAT.fieldOf("head_yaw").forGetter(TemporalPosition::headYaw),
                Codec.FLOAT.fieldOf("body_yaw").forGetter(TemporalPosition::bodyYaw),
                Codec.STRING.fieldOf("entity_pose")
                    .xmap({ EntityPose.valueOf(it) }, { it.name })
                    .forGetter(TemporalPosition::entityPose),
                Codec.LONG.fieldOf("time").forGetter(TemporalPosition::time)
            ).apply(instance, ::TemporalPosition)
        }
    }
}

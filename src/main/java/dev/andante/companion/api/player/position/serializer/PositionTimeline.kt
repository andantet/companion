package dev.andante.companion.api.player.position.serializer

import com.google.gson.JsonObject
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.andante.companion.api.player.position.TemporalPosition

/**
 * A timeline of temporal positions.
 */
class PositionTimeline(
    /**
     * All positions.
     */
    val positions: List<TemporalPosition>
) {
    /**
     * All positions by their time.
     */
    val mappedPositions: Map<Long, TemporalPosition> = positions.associateBy(TemporalPosition::time)

    /**
     * Whether the given time is greater than the last time.
     */
    fun isFinished(time: Long): Boolean {
        return time > mappedPositions.keys.last()
    }

    /**
     * Converts this timeline to a json object.
     */
    fun toJson(): JsonObject {
        return CODEC.encodeStart(JsonOps.INSTANCE, this)
            .result()
            .orElseThrow() as JsonObject
    }

    companion object {
        /**
         * The codec of a position timeline.
         */
        val CODEC: Codec<PositionTimeline> = RecordCodecBuilder.create { instance ->
            instance.group(
                TemporalPosition.CODEC.listOf().fieldOf("positions").forGetter(PositionTimeline::positions)
            ).apply(instance, ::PositionTimeline)
        }
    }
}

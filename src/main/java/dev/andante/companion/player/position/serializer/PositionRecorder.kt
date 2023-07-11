package dev.andante.companion.player.position.serializer

import dev.andante.companion.player.position.TemporalPosition
import net.minecraft.text.Text
import net.minecraft.util.math.Direction.Axis
import java.util.EnumSet

data class PositionRecorder(
    /**
     * The time that this serializer began measuring from.
     */
    val baseTime: Long
) {
    /**
     * All recorded positions.
     */
    private val positions = mutableListOf<TemporalPosition>()

    /**
     * The previous temporal position.
     */
    private var previousPosition: TemporalPosition? = null

    /**
     * Adds a position to the record.
     */
    fun add(temporalPosition: TemporalPosition) {
        // skip adding a position if it is the same as the previous
        val pos = previousPosition
        if (pos == null || !pos.equalsIgnoreTime(temporalPosition)) {
            positions.add(temporalPosition)
            previousPosition = temporalPosition
        }
    }

    /**
     * Compiles this serializer to a timeline.
     */
    fun compile(): PositionTimeline {
        val compiledPositions = positions.map { position -> position.copy(time = position.time - baseTime) }
        return PositionTimeline(compiledPositions)
    }

    fun renderDebugHud(textRendererConsumer: (Text) -> Unit) {
        previousPosition?.run {
            textRendererConsumer(Text.literal("pos: ${pos.floorAlongAxes(EnumSet.allOf(Axis::class.java))}, time: $time (${time / 20}s)"))
        }

        textRendererConsumer(Text.literal("Positions size: ${positions.size}"))
    }
}

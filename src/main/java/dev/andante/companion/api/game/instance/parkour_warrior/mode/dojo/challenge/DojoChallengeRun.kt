package dev.andante.companion.api.game.instance.parkour_warrior.mode.dojo.challenge

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.andante.companion.api.game.instance.parkour_warrior.ParkourWarriorSection
import dev.andante.companion.api.game.instance.parkour_warrior.mode.dojo.DojoDifficulty
import dev.andante.companion.api.player.PlayerReference
import dev.andante.companion.api.player.position.serializer.PositionTimeline
import net.minecraft.util.Uuids
import java.util.Optional
import java.util.UUID

/**
 * Represents a run of Parkour Warrior: Dojo challenge mode.
 */
data class DojoChallengeRun(
    /**
     * The uuid of this run.
     */
    val uuid: UUID,

    /**
     * A reference to the player who ran this challenge run.
     */
    val runner: Optional<PlayerReference>,

    /**
     * The number of the course that this challenge run was on.
     */
    val courseNumber: Int,

    /**
     * The timestamp that this challenge run ended at.
     */
    val timestampMs: Long,

    /**
     * The measured duration of this run in milliseconds.
     */
    val durationMs: Long,

    /**
     * The duration string as provided by MCC: Island.
     */
    val durationString: Optional<String>,

    /**
     * The number of medals gained throughout the run.
     */
    val medalsGained: Int,

    /**
     * The completion type of the run.
     */
    val completionType: DojoCompletionType,

    /**
     * The difficulty chosen at the last section of the run.
     */
    val endingDifficulty: Optional<DojoDifficulty>,

    /**
     * A list of the sections completed throughout the run.
     */
    val completedSections: List<ParkourWarriorSection>,

    /**
     * A timeline of the runner's position throughout the run.
     */
    val positionTimeline: PositionTimeline
) {
    companion object {
        /**
         * The codec of this class.
         */
        val CODEC: Codec<DojoChallengeRun> = RecordCodecBuilder.create { instance ->
            instance.group(
                Uuids.CODEC.fieldOf("uuid").forGetter(DojoChallengeRun::uuid),
                PlayerReference.CODEC.optionalFieldOf("runner").forGetter(DojoChallengeRun::runner),
                Codec.INT.fieldOf("course_number").forGetter(DojoChallengeRun::courseNumber),
                Codec.LONG.fieldOf("timestamp_ms").forGetter(DojoChallengeRun::timestampMs),
                Codec.LONG.fieldOf("duration_ms").forGetter(DojoChallengeRun::durationMs),
                Codec.STRING.optionalFieldOf("duration").forGetter(DojoChallengeRun::durationString),
                Codec.INT.fieldOf("medals_gained").forGetter(DojoChallengeRun::medalsGained),
                DojoCompletionType.CODEC.fieldOf("completion_type").forGetter(DojoChallengeRun::completionType),
                DojoDifficulty.CODEC.optionalFieldOf("ending_difficulty").forGetter(DojoChallengeRun::endingDifficulty),
                ParkourWarriorSection.CODEC.listOf().fieldOf("completed_sections").forGetter(DojoChallengeRun::completedSections),
                PositionTimeline.CODEC.fieldOf("position_timeline").forGetter(DojoChallengeRun::positionTimeline),
            ).apply(instance, ::DojoChallengeRun)
        }
    }
}

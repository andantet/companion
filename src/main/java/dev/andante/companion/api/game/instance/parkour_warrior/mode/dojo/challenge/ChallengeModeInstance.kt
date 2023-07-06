package dev.andante.companion.api.game.instance.parkour_warrior.mode.dojo.challenge

import com.google.gson.GsonBuilder
import com.mojang.serialization.JsonOps
import dev.andante.companion.api.game.instance.parkour_warrior.ParkourWarriorInstance
import dev.andante.companion.api.game.instance.parkour_warrior.ParkourWarriorSection
import dev.andante.companion.api.game.instance.parkour_warrior.mode.dojo.DojoDifficulty
import dev.andante.companion.api.game.instance.parkour_warrior.mode.dojo.DojoModeInstance
import dev.andante.companion.api.player.PlayerReference
import dev.andante.companion.api.player.position.serializer.PositionRecorderManager
import dev.andante.companion.api.player.position.serializer.PositionTimeline
import dev.andante.companion.api.setting.MetricsSettings
import dev.andante.companion.api.setting.MusicSettings
import net.minecraft.client.MinecraftClient
import net.minecraft.client.world.ClientWorld
import net.minecraft.text.Text
import net.minecraft.util.Util

/**
 * An instance of Parkour Warrior Dojo challenge mode.
 */
class ChallengeModeInstance(world: ClientWorld) : DojoModeInstance(
    { MusicSettings.INSTANCE.parkourWarriorDojoChallengeModeMusic }
) {
    /**
     * The file of this mode's run.
     */
    private val file = DojoChallengeRunManager.RUNS_DIRECTORY.resolve("$uuid.json")

    /**
     * The time that the run started at.
     */
    private val startedAt = Util.getMeasuringTimeMs()

    /**
     * The current duration of the run.
     */
    private val durationMs get() = Util.getMeasuringTimeMs() - startedAt

    /**
     * This run's completed sections.
     */
    private val completedSections = mutableListOf<CompletedSection>()

    /**
     * The duration of the run as provided by a string.
     */
    private var durationString: String? = null

    /**
     * This run's concurrent medals.
     */
    private var medalsGained = 0

    /**
     * The completion type of this run.
     */
    private var completionType: DojoCompletionType = DojoCompletionType.INCOMPLETE

    /**
     * The difficulty of the ending section taken.
     */
    private val endingDifficulty: DojoDifficulty? get() =
        if (completionType == DojoCompletionType.INCOMPLETE) {
            null
        } else {
            DojoDifficulty.endingMedalsAssociation(medalsGained - completedSections.size + 1)
        }

    /**
     * The position recorder for this instance.
     */
    private val positionRecorder = PositionRecorderManager.create(world)

    override fun onSectionUpdate(section: ParkourWarriorSection?, previousSection: ParkourWarriorSection?, medals: Int) {
        if (section == null && previousSection != null) {
            completedSections.add(previousSection.toCompleted(startedAt))
        }

        medalsGained += medals
    }

    override fun onCourseCompleted(medals: Int, time: String, completionType: DojoCompletionType) {
        // set values
        medalsGained = medals
        durationString = time
        this.completionType = completionType

        // add last section
        currentSection?.let { section -> completedSections.add(section.toCompleted(startedAt)) }

        // flush to json
        if (MetricsSettings.INSTANCE.parkourWarriorMetrics) {
            val positionTimeline = PositionRecorderManager.removeAndCompile(positionRecorder)
            flushToJson(positionTimeline)
        } else {
            PositionRecorderManager.remove(positionRecorder)
        }
    }

    override fun onCourseRestart(): Boolean {
        // set values
        completionType = DojoCompletionType.INCOMPLETE

        // flush to json
        if (MetricsSettings.INSTANCE.parkourWarriorMetrics) {
            val positionTimeline = PositionRecorderManager.removeAndCompile(positionRecorder)
            flushToJson(positionTimeline)
        } else {
            PositionRecorderManager.remove(positionRecorder)
        }

        // clear instance
        return true
    }

    override fun renderDebugHud(textRendererConsumer: (Text) -> Unit) {
        textRendererConsumer(Text.literal("Run: $uuid"))

        if (dailyChallenge) {
            textRendererConsumer(Text.literal("Daily Challenge"))
        }

        positionRecorder.renderDebugHud(textRendererConsumer)
    }

    private fun flushToJson(positionTimeline: PositionTimeline) {
        ParkourWarriorInstance.LOGGER.info("Flushing challenge instance to JSON: ${file.path}")

        val profile = MinecraftClient.getInstance().session.profile
        val reference = PlayerReference(profile.id, profile.name)
        val run = DojoChallengeRun(
            uuid,
            reference,
            courseNumber,
            dailyChallenge,
            System.currentTimeMillis(),
            durationMs,
            durationString,
            medalsGained,
            completionType,
            endingDifficulty,
            completedSections,
            positionTimeline
        )

        val json = DojoChallengeRun.CODEC.encodeStart(JsonOps.INSTANCE, run)
            .result()
            .orElseThrow()

        val gson = GsonBuilder().setPrettyPrinting().create()
        val jsonString = gson.toJson(json)
        file.parentFile.mkdirs()
        file.writeText(jsonString)
    }
}

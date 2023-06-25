package dev.andante.companion.api.game.instance.parkour_warrior_dojo.mode

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import dev.andante.companion.api.game.instance.parkour_warrior_dojo.ParkourWarriorDojoInstance
import dev.andante.companion.api.game.type.GameTypes
import dev.andante.companion.api.helper.FileHelper
import dev.andante.companion.api.player.position.serializer.PositionRecorderManager
import dev.andante.companion.api.player.position.serializer.PositionTimeline
import dev.andante.companion.setting.MetricsSettings
import dev.andante.companion.setting.MusicSettings
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
    private val file = FileHelper.companionFile("game_instances/${GameTypes.PARKOUR_WARRIOR_DOJO.id}/runs/$uuid.json")

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
    private val completedSections = mutableListOf<Section>()

    /**
     * The duration of the run as provided by a string.
     */
    private lateinit var durationString: String

    /**
     * This run's concurrent medals.
     */
    private var medalsGained = 0

    /**
     * The completion type of this run.
     */
    private var completionType: CompletionType = CompletionType.INCOMPLETE

    /**
     * The difficulty of the ending section taken.
     */
    private val endingDifficulty: Difficulty? get() = Difficulty.endingMedalsAssociation(medalsGained - completedSections.size + 1)

    /**
     * The position recorder for this instance.
     */
    private val positionRecorder = PositionRecorderManager.create(world)

    override fun onSectionUpdate(section: Section?, previousSection: Section?, medals: Int) {
        if (section == null && previousSection != null) {
            completedSections.add(previousSection)
        }

        medalsGained += medals
    }

    override fun onCourseCompleted(medals: Int, time: String, completionType: CompletionType) {
        // set values
        medalsGained = medals
        durationString = time
        this.completionType = completionType

        // add last section
        currentSection?.let(completedSections::add)

        // flush to json
        if (MetricsSettings.INSTANCE.parkourWarriorDojoMetrics) {
            val positionTimeline = PositionRecorderManager.removeAndCompile(positionRecorder)
            flushToJson(positionTimeline)
        } else {
            PositionRecorderManager.remove(positionRecorder)
        }
    }

    override fun onCourseRestart(): Boolean {
        // set values
        durationString = ""
        completionType = CompletionType.INCOMPLETE

        // flush to json
        if (MetricsSettings.INSTANCE.parkourWarriorDojoMetrics) {
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
        positionRecorder.renderDebugHud(textRendererConsumer)
    }

    private fun flushToJson(positionTimeline: PositionTimeline) {
        ParkourWarriorDojoInstance.LOGGER.info("Flushing challenge instance to JSON: ${file.path}")

        val json = JsonObject()

        json.addProperty("course_number", courseNumber)
        json.addProperty("timestamp_ms", System.currentTimeMillis())
        json.addProperty("duration_ms", durationMs)
        json.addProperty("duration", durationString)
        json.addProperty("medals_gained", medalsGained)
        json.addProperty("completion_type", completionType.name)

        if (completionType != CompletionType.INCOMPLETE) {
            endingDifficulty?.let { json.addProperty("ending_difficulty", it.name) }
        }

        val completedSectionsJson = JsonArray()
        completedSections.forEach { section ->
            completedSectionsJson.add(section.toJson())
        }
        json.add("completed_sections", completedSectionsJson)

        json.add("position_timeline", positionTimeline.toJson())

        val gson = GsonBuilder().setPrettyPrinting().create()
        val jsonString = gson.toJson(json)
        file.parentFile.mkdirs()
        file.writeText(jsonString)
    }
}

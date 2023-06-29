package dev.andante.companion.api.game.instance.parkour_warrior_dojo

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.JsonOps
import dev.andante.companion.api.game.type.GameTypes
import dev.andante.companion.api.helper.FileHelper.companionFile
import dev.andante.companion.api.player.ghost.GhostPlayerManager
import dev.andante.companion.api.player.position.serializer.PositionTimeline
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileFilter
import java.util.concurrent.CompletableFuture

object DojoRunManager {
    private val LOGGER: Logger = LoggerFactory.getLogger("[MCCI: Companion / Parkour Warrior Dojo] Run Manager")

    /**
     * The folder where runs are stored.
     */
    val RUNS_FOLDER = companionFile("game_instances/${GameTypes.PARKOUR_WARRIOR_DOJO.id}/runs")

    /**
     * Loaded run position timelines.
     */
    private val runTimelines = mutableMapOf<String, PositionTimeline>()

    /**
     * Lists all run files stored in the runs folder.
     */
    fun listRunFiles(): List<File> {
        return RUNS_FOLDER.listFiles(FileFilter { it.extension == "json" })?.toList() ?: emptyList()
    }

    /**
     * Reloads run timelines from disk.
     */
    fun reloadRunTimelines(): Int {
        // clear ghosts (ghosts detach from their registered timelines)
        GhostPlayerManager.clear()

        // clear previous timelines
        runTimelines.clear()

        // load new timelines
        var count = 0
        listRunFiles().forEach { file ->
            try {
                val fileName = file.nameWithoutExtension

                val text = file.readText()
                val json = JsonParser.parseString(text) as JsonObject
                val timelineJson = json.getAsJsonObject("position_timeline")

                val timeline = PositionTimeline.CODEC.decode(JsonOps.INSTANCE, timelineJson)
                    .map(Pair<PositionTimeline, *>::getFirst)
                    .result()
                    .orElseThrow()

                runTimelines[fileName] = timeline
                count++
            } catch (exception: Exception) {
                LOGGER.error("Could not parse run file: $file", exception)
            }
        }

        return count
    }

    operator fun get(id: String): PositionTimeline? {
        return runTimelines[id]
    }

    /**
     * Suggests the loaded runs to the given suggestions builder.
     */
    fun suggestRuns(builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        runTimelines.keys.forEach(builder::suggest)
        return builder.buildFuture()
    }
}

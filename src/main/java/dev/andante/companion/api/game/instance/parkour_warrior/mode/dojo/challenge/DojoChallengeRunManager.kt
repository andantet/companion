package dev.andante.companion.api.game.instance.parkour_warrior.mode.dojo.challenge

import com.google.gson.JsonParser
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.JsonOps
import dev.andante.companion.api.game.type.GameTypes
import dev.andante.companion.api.helper.FileHelper.companionFile
import dev.andante.companion.api.player.ghost.GhostPlayerManager
import net.fabricmc.loader.api.FabricLoader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileFilter
import java.util.concurrent.CompletableFuture

object DojoChallengeRunManager {
    private val LOGGER: Logger = LoggerFactory.getLogger("[MCCI: Companion / Parkour Warrior: Dojo] Challenge Run Manager")

    /**
     * The folder where runs are stored.
     */
    val RUNS_DIRECTORY = companionFile("game_instances/${GameTypes.PARKOUR_WARRIOR.id}/dojo_runs")

    /**
     * Loaded challenge runs.
     */
    private val challengeRuns = mutableMapOf<String, DojoChallengeRun>()

    /**
     * Lists all run files stored in the runs folder.
     */
    fun listRunFiles(): List<File> {
        return RUNS_DIRECTORY.listFiles(FileFilter { it.extension == "json" })?.toList() ?: emptyList()
    }

    /**
     * Reloads all challenge runs from disk.
     */
    fun reload(): Int {
        // clear previous runs
        challengeRuns.clear()

        // load new runs
        var count = 0
        listRunFiles().forEach { file ->
            try {
                val fileName = file.nameWithoutExtension

                val text = file.readText()
                val json = JsonParser.parseString(text)

                val run = DojoChallengeRun.CODEC.decode(JsonOps.INSTANCE, json)
                    .map(Pair<DojoChallengeRun, *>::getFirst)
                    .result()
                    .orElseThrow()

                challengeRuns[fileName] = run
                count++
            } catch (exception: Exception) {
                if (FabricLoader.getInstance().isDevelopmentEnvironment) {
                    LOGGER.error("Could not parse run file", exception)
                }

                // ignore file if it cannot be parsed
            }
        }

        // verify that ghosts are still valid
        GhostPlayerManager.tryInvalidatePlayers(challengeRuns.mapValues { it.value.positionTimeline })

        return count
    }

    operator fun get(id: String): DojoChallengeRun? {
        return challengeRuns[id]
    }

    /**
     * Suggests the loaded runs to the given suggestions builder.
     */
    fun suggestRuns(builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        challengeRuns.keys.forEach(builder::suggest)
        return builder.buildFuture()
    }
}

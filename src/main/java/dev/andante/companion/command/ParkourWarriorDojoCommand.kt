package dev.andante.companion.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import dev.andante.companion.Companion
import dev.andante.companion.api.game.GameTracker
import dev.andante.companion.api.game.instance.parkour_warrior_dojo.DojoRunManager
import dev.andante.companion.api.game.type.GameTypes
import dev.andante.companion.api.player.ghost.GhostPlayerManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.command.argument.UuidArgumentType
import net.minecraft.text.Text
import java.io.File
import java.util.UUID

object ParkourWarriorDojoCommand {
    private val NO_RUNS_FOUND_EXCEPTION = SimpleCommandExceptionType(Text.translatable("command.${Companion.MOD_ID}.parkour_warrior_dojo.runs.noRunsFound"))
    private val NO_RUN_FOUND_EXCEPTION = SimpleCommandExceptionType(Text.translatable("command.${Companion.MOD_ID}.parkour_warrior_dojo.runs.noRunFound"))

    private val NOT_IN_PARKOUR_WARRIOR_DOJO_EXCEPTION = SimpleCommandExceptionType(Text.translatable("command.${Companion.MOD_ID}.parkour_warrior_dojo.notPresent"))

    private const val ADDED_GHOST_KEY = "command.${Companion.MOD_ID}.parkour_warrior_dojo.runs.ghostAdded"
    private const val REMOVED_GHOST_KEY = "command.${Companion.MOD_ID}.parkour_warrior_dojo.runs.ghostRemoved"

    private val CLEARED_ALL_GHOSTS_MESSAGE = Text.translatable("command.${Companion.MOD_ID}.parkour_warrior_dojo.runs.ghostsCleared")

    private const val UUID_KEY = "uuid"

    fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        dispatcher.register(
            ClientCommandManager.literal("${Companion.MOD_ID}:parkour_warrior_dojo")
                .then(
                    ClientCommandManager.literal("runs")
                        .executes(::executeListRuns)
                        .then(
                            ClientCommandManager.literal("ghost")
                                .then(
                                    ClientCommandManager.argument(UUID_KEY, UuidArgumentType.uuid())
                                        .suggests { _, builder -> DojoRunManager.suggestRuns(builder) }
                                        .executes { context -> executeGhost(context, false) }
                                        .then(
                                            ClientCommandManager.literal("repeat")
                                                .executes { context -> executeGhost(context, true) }
                                        )
                                )
                                .then(
                                    ClientCommandManager.literal("clear")
                                        .executes(::executeGhostClear)
                                )
                        )
                )
        )
    }

    @Throws(CommandSyntaxException::class)
    private fun executeListRuns(context: CommandContext<FabricClientCommandSource>): Int {
        val files = DojoRunManager.listRunFiles()
        if (files.isNotEmpty()) {
            context.source.sendFeedback(Text.literal("Runs: ${files.joinToString(transform = File::nameWithoutExtension)}"))
        } else {
            throw NO_RUNS_FOUND_EXCEPTION.create()
        }
        return 1
    }

    private fun executeGhost(context: CommandContext<FabricClientCommandSource>, repeat: Boolean): Int {
        if (GameTracker.gameType != GameTypes.PARKOUR_WARRIOR_DOJO) {
            throw NOT_IN_PARKOUR_WARRIOR_DOJO_EXCEPTION.create()
        }

        val uuid = context.getArgument(UUID_KEY, UUID::class.java)
        val timeline = DojoRunManager[uuid] ?: throw NO_RUN_FOUND_EXCEPTION.create()
        if (GhostPlayerManager.remove(timeline)) {
            context.source.sendFeedback(Text.translatable(REMOVED_GHOST_KEY, uuid))
        } else {
            GhostPlayerManager.add(timeline, repeat)
            context.source.sendFeedback(Text.translatable(ADDED_GHOST_KEY, uuid))
        }

        return 1
    }

    private fun executeGhostClear(context: CommandContext<FabricClientCommandSource>): Int {
        GhostPlayerManager.clear()
        context.source.sendFeedback(CLEARED_ALL_GHOSTS_MESSAGE)
        return 1
    }
}

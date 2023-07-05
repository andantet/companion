package dev.andante.companion.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import dev.andante.companion.Companion
import dev.andante.companion.api.game.GameTracker
import dev.andante.companion.api.game.instance.parkour_warrior.mode.dojo.challenge.DojoChallengeRunManager
import dev.andante.companion.api.game.type.GameTypes
import dev.andante.companion.api.player.ghost.GhostPlayerManager
import dev.andante.companion.api.player.position.serializer.IdentifiablePositionTimeline
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.Style
import net.minecraft.text.Text
import java.io.File

object ParkourWarriorDojoCommand {
    private val FEEDBACK_STYLE = Style.EMPTY.withColor(0xFFE72B)
    private val GHOST_FEEDBACK_STYLE = Style.EMPTY.withColor(0x8E8ED5)

    private val NO_RUNS_FOUND_EXCEPTION = SimpleCommandExceptionType(Text.translatable("command.${Companion.MOD_ID}.parkour_warrior_dojo.runs.noRunsFound"))
    private val NO_RUN_FOUND_EXCEPTION = SimpleCommandExceptionType(Text.translatable("command.${Companion.MOD_ID}.parkour_warrior_dojo.runs.noRunFound"))

    private val NOT_IN_PARKOUR_WARRIOR_DOJO_EXCEPTION = SimpleCommandExceptionType(Text.translatable("command.${Companion.MOD_ID}.parkour_warrior_dojo.notPresent"))

    private const val RUNS_LIST_KEY = "command.${Companion.MOD_ID}.parkour_warrior_dojo.runs.list"
    private const val RELOADED_RUNS_KEY = "command.${Companion.MOD_ID}.parkour_warrior_dojo.runs.reloaded"

    private const val ADDED_GHOST_KEY = "command.${Companion.MOD_ID}.parkour_warrior_dojo.runs.ghostAdded"
    private const val REMOVED_GHOST_KEY = "command.${Companion.MOD_ID}.parkour_warrior_dojo.runs.ghostRemoved"

    private val CLEARED_ALL_GHOSTS_MESSAGE = Text.translatable("command.${Companion.MOD_ID}.parkour_warrior_dojo.runs.ghostsCleared").setStyle(GHOST_FEEDBACK_STYLE)

    private const val ID_KEY = "id"

    fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        dispatcher.register(
            literal("${Companion.MOD_ID}:parkour_warrior_dojo")
                .then(
                    literal("runs")
                        .then(
                            literal("list")
                                .executes(::executeListRuns)
                        )
                        .then(
                            literal("reload")
                                .executes(::executeReloadRuns)
                        )
                )
                .then(
                    literal("ghosts")
                        .then(
                            literal("toggle")
                                .then(
                                    argument(ID_KEY, StringArgumentType.string())
                                        .suggests { _, builder -> DojoChallengeRunManager.suggestRuns(builder) }
                                        .executes { context -> executeGhost(context, false) }
                                        .then(
                                            literal("repeat")
                                                .executes { context -> executeGhost(context, true) }
                                        )
                                )
                        )
                        .then(
                            literal("clear")
                                .executes(::executeGhostClear)
                        )
                )
        )
    }

    @Throws(CommandSyntaxException::class)
    private fun executeListRuns(context: CommandContext<FabricClientCommandSource>): Int {
        val files = DojoChallengeRunManager.listRunFiles()
        if (files.isNotEmpty()) {
            context.source.sendFeedback(Text.translatable(RUNS_LIST_KEY, files.joinToString(transform = File::nameWithoutExtension, prefix = "- ", separator = "\n- ")).setStyle(FEEDBACK_STYLE))
        } else {
            throw NO_RUNS_FOUND_EXCEPTION.create()
        }
        return 1
    }

    private fun executeReloadRuns(context: CommandContext<FabricClientCommandSource>): Int {
        val count = DojoChallengeRunManager.reload()
        context.source.sendFeedback(Text.translatable(RELOADED_RUNS_KEY, count).setStyle(FEEDBACK_STYLE))
        return 1
    }

    private fun executeGhost(context: CommandContext<FabricClientCommandSource>, repeat: Boolean): Int {
        if (GameTracker.gameType != GameTypes.PARKOUR_WARRIOR) {
            throw NOT_IN_PARKOUR_WARRIOR_DOJO_EXCEPTION.create()
        }

        val id = StringArgumentType.getString(context, ID_KEY)
        val challengeRun = DojoChallengeRunManager[id] ?: throw NO_RUN_FOUND_EXCEPTION.create()
        val positionTimeline = IdentifiablePositionTimeline(id, challengeRun.positionTimeline)
        if (GhostPlayerManager.remove(positionTimeline)) {
            context.source.sendFeedback(Text.translatable(REMOVED_GHOST_KEY, id).setStyle(GHOST_FEEDBACK_STYLE))
        } else {
            GhostPlayerManager.add(positionTimeline, repeat)
            context.source.sendFeedback(Text.translatable(ADDED_GHOST_KEY, id).setStyle(GHOST_FEEDBACK_STYLE))
        }

        return 1
    }

    private fun executeGhostClear(context: CommandContext<FabricClientCommandSource>): Int {
        GhostPlayerManager.clear()
        context.source.sendFeedback(CLEARED_ALL_GHOSTS_MESSAGE.setStyle(GHOST_FEEDBACK_STYLE))
        return 1
    }
}

package dev.andante.companion.api.game.round

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import dev.andante.companion.api.game.instance.RoundBasedGameInstance
import net.minecraft.text.Text
import org.intellij.lang.annotations.RegExp

open class RoundManager<T : RoundBasedGameInstance<T>, R : Round<T>>(
    /**
     * The game instance.
     */
    private val gameInstance: RoundBasedGameInstance<T>,

    /**
     * Creates a new round from the given factory.
     */
    private val roundFactory: RoundFactory<R>
) {
    /**
     * The current state of the round manager.
     */
    private var state: State = State.GAME_NOT_STARTED

    /**
     * The current round number.
     */
    var currentRound = -1

    /**
     * The current round instance.
     */
    var round: R = roundFactory.create()

    /**
     * Stored previous rounds.
     */
    val allRounds: MutableList<R> = mutableListOf()

    /**
     * Whether the round is currently in progress.
     */
    val roundInProgress get() = state == State.IN_PROGRESS

    /**
     * Whether the round is starting.
     */
    val roundStarting get() = state == State.INITIALIZED

    /**
     * Whether the round has finished.
     */
    val roundFinished get() = state == State.FINISHED

    /**
     * Whether the game has started.
     */
    val gameStarted get() = state != State.GAME_NOT_STARTED

    /**
     * Whether the game has ended.
     */
    val gameEnded get() = state == State.GAME_ENDED

    fun onGameMessage(text: Text) {
        val raw: String = text.string

        if (raw.matches(FACING_TEAM_REGEX)) {
            initialize()
        } else if (raw.matches(GAME_STARTED_REGEX)) {
            initialize()
            start()
        } else if (raw.matches(ROUND_STARTED_REGEX)) {
            start()
        } else if (raw.matches(ROUND_OVER_REGEX)) {
            finish()
        } else if (raw.matches(GAME_OVER_REGEX)) {
            if (state == State.IN_PROGRESS) {
                finish()
            }

            endGame()
        }
    }

    /**
     * Initializes the next round.
     */
    fun initialize() {
        // increment current round
        currentRound++

        // create new round
        round = roundFactory.create()

        // set state
        state = State.INITIALIZED

        // call game instance listener
        gameInstance.onRoundInitialize(round, currentRound == 0)
    }

    /**
     * Starts the current round.
     */
    private fun start() {
        val isFirstRound = currentRound == 0

        // set state
        state = State.IN_PROGRESS

        if (isFirstRound) {
            // call game instance listener
            gameInstance.onGameStart(round)
        }

        // call game instance listener
        gameInstance.onRoundStart(round, isFirstRound)
    }

    /**
     * Finishes the current round.
     */
    fun finish() {
        // call game instance listener
        gameInstance.onRoundFinish(round)

        // add that round to storage
        allRounds.add(round)

        // set state
        state = State.FINISHED
    }

    /**
     * Ends the game at the final round.
     */
    private fun endGame() {
        // set state
        state = State.GAME_ENDED

        // call game instance listener
        gameInstance.onGameEnd(round)
    }

    fun renderDebugHud(textRendererConsumer: (Text) -> Unit) {
        textRendererConsumer(Text.literal("${state.name}, $currentRound"))
    }

    fun toJson(): JsonArray {
        val roundsJson = JsonArray()
        allRounds.forEach { round ->
            val roundJson = JsonObject()
            round.toJson(roundJson)
            roundsJson.add(roundJson)
        }
        return roundsJson
    }

    /**
     * The current state of the round manager.
     */
    enum class State {
        GAME_NOT_STARTED,
        INITIALIZED,
        IN_PROGRESS,
        FINISHED,
        GAME_ENDED
    }

    companion object {
        const val NBT_KEY = "rounds"

        @RegExp
        val FACING_TEAM_REGEX = Regex("\\[.] You are facing the . \\w+ Team!")

        @RegExp
        val ROUND_OVER_REGEX = Regex("\\[.] Round [0-9]+ over!")

        @RegExp
        val GAME_STARTED_REGEX = Regex("\\[.] Game started!")

        @RegExp
        val ROUND_STARTED_REGEX = Regex("\\[.] Round [0-9]+ started!")

        @RegExp
        val GAME_OVER_REGEX = Regex("\\[.] Game Over!")
    }
}

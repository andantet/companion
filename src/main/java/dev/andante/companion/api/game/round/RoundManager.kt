package dev.andante.companion.api.game.round

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import dev.andante.companion.api.game.instance.RoundBasedGameInstance
import net.minecraft.text.Text
import org.intellij.lang.annotations.RegExp

open class RoundManager<R : Round, T : RoundBasedGameInstance<out R, T>>(
    /**
     * The game instance.
     */
    private val gameInstance: RoundBasedGameInstance<out R, T>,

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
    private var currentRound = -1

    /**
     * The current round instance.
     */
    private var round: R = roundFactory.create(currentRound)

    /**
     * Stored previous rounds.
     */
    private val allRounds: MutableList<R> = mutableListOf()

    fun onTitle(text: Text) {
        val string = text.string
        if (string.matches(ROUND_NUMBER_TITLE)) {
            initialize()
        }
    }

    fun onGameMessage(text: Text) {
        val string = text.string
        if (string.matches(FACING_TEAM_REGEX)) {
            initialize()
        } else if (string.matches(GAME_STARTED_REGEX)) {
            initialize()
            start()
        } else if (string.matches(ROUND_STARTED_REGEX)) {
            if (state != State.INITIALIZED) {
                initialize()
            }

            start()
        } else if (string.matches(ROUND_OVER_REGEX)) {
            finish()
        } else if (string.matches(GAME_OVER_REGEX) || string.matches(GAME_FINISHED_REGEX)) {
            if (state != State.FINISHED) {
                finish()
            }

            endGame()
        } else {
            round.onGameMessage(text)
        }
    }

    /**
     * Initializes the next round.
     */
    private fun initialize() {
        // increment current round
        currentRound++

        // create new round
        round = roundFactory.create(currentRound)

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
    private fun finish() {
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
        round.renderDebugHud(textRendererConsumer)
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
        const val SERIALIZATION_KEY = "rounds"

        @RegExp
        val FACING_TEAM_REGEX = Regex("\\[.] You are facing the . \\w+ Team!")

        @RegExp
        val ROUND_NUMBER_TITLE = Regex("Round [0-9]+")

        @RegExp
        val GAME_STARTED_REGEX = Regex("\\[.] Game started!")

        @RegExp
        val ROUND_STARTED_REGEX = Regex("\\[.] Round [0-9]+ started!")

        @RegExp
        val ROUND_OVER_REGEX = Regex("\\[.] Round [0-9]+ over!")

        @RegExp
        val GAME_FINISHED_REGEX = Regex("\\[.] You finished the game and came in [0-9]+.. place!")

        @RegExp
        val GAME_OVER_REGEX = Regex("\\[.] Game Over!")
    }
}

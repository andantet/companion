package dev.andante.companion.api.game.round

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import dev.andante.companion.api.game.instance.RoundBasedGameInstance
import dev.andante.companion.api.regex.RegexKeys
import dev.andante.companion.api.regex.RegexManager
import dev.andante.companion.api.sound.CompanionSoundManager
import dev.andante.companion.api.sound.CompanionSounds
import dev.andante.companion.setting.MusicSettings
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text

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

    fun tick(client: MinecraftClient) {
        round.tick(client)
    }

    fun onTitle(text: Text) {
        val string = text.string
        if (RegexManager.matches(RegexKeys.ROUND_NUMBER_TITLE, string)) {
            initialize()
        }
    }

    fun onGameMessage(text: Text) {
        val string = text.string
        if (RegexManager.matches(RegexKeys.FACING_TEAM, string)) {
            initialize()
        } else if (RegexManager.matches(RegexKeys.GAME_STARTED, string)) {
            initialize()
            start()
        } else if (RegexManager.matches(RegexKeys.ROUND_STARTED, string)) {
            initialize()
            start()
        } else if (RegexManager.matches(RegexKeys.ROUND_OVER, string)) {
            finish()
        } else if (RegexManager.matches(RegexKeys.GAME_OVER, string) || RegexManager.matches(RegexKeys.GAME_FINISHED, string)) {
            if (state != State.FINISHED) {
                finish()
                CompanionSoundManager.play(CompanionSounds.MUSIC_ROUNDENDMUSIC) { MusicSettings.INSTANCE.musicVolume }
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
        if (state == State.INITIALIZED || state == State.IN_PROGRESS) {
            return
        }

        // set state
        state = State.INITIALIZED

        // increment current round
        currentRound++

        // create new round
        round = roundFactory.create(currentRound)

        // call game instance listener
        gameInstance.onRoundInitialize(round, currentRound == 0)
    }

    /**
     * Starts the current round.
     */
    private fun start() {
        if (state == State.IN_PROGRESS) {
            return
        }

        // set state
        state = State.IN_PROGRESS

        val isFirstRound = currentRound == 0

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
        if (state == State.FINISHED || state == State.GAME_ENDED) {
            return
        }

        // set state
        state = State.FINISHED

        // call game instance listener
        gameInstance.onRoundFinish(round)

        // add that round to storage
        allRounds.add(round)
    }

    /**
     * Ends the game at the final round.
     */
    private fun endGame() {
        if (state == State.GAME_ENDED) {
            return
        }

        // set state
        state = State.GAME_ENDED

        // call game instance listener
        gameInstance.onGameEnd(round)
    }

    fun renderDebugHud(textRendererConsumer: (Text) -> Unit) {
        textRendererConsumer(Text.literal("State: $state, Round: $currentRound"))
        round.renderDebugHud(textRendererConsumer)
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
    }
}

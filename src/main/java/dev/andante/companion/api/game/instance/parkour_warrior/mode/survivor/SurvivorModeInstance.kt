package dev.andante.companion.api.game.instance.parkour_warrior.mode.survivor

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import dev.andante.companion.api.game.instance.parkour_warrior.ParkourWarriorInstance
import dev.andante.companion.api.game.instance.parkour_warrior.ParkourWarriorSection
import dev.andante.companion.api.game.instance.parkour_warrior.mode.ParkourWarriorModeInstance
import dev.andante.companion.api.game.type.GameTypes
import dev.andante.companion.api.helper.FileHelper.companionFile
import dev.andante.companion.api.regex.RegexKeys
import dev.andante.companion.api.regex.RegexManager
import dev.andante.companion.api.setting.MusicSettings
import dev.andante.companion.api.sound.CompanionSoundManager
import dev.andante.companion.api.sound.CompanionSounds
import net.minecraft.client.sound.SoundInstance
import net.minecraft.text.Text
import net.minecraft.util.StringIdentifiable

class SurvivorModeInstance : ParkourWarriorModeInstance({ MusicSettings.INSTANCE.parkourWarriorSurvivorMusic }) {
    /**
     * The folder where survivor mode instances are stored.
     */
    val file = companionFile("game_instances/${GameTypes.PARKOUR_WARRIOR.id}/$uuid.json")

    /**
     * The current leap of the course.
     */
    private var state: State = State.GAME_NOT_STARTED

    /**
     * The number of the current leap.
     */
    private var currentLeapNumber: Int = 0

    /**
     * The current leap.
     */
    private var currentLeap: Leap = Leap(-1)

    /**
     * The runner's placement.
     */
    private var placement: Int = -1

    /**
     * Stored previous leaps.
     */
    private val allLeaps: MutableList<Leap> = mutableListOf()

    override fun onTitle(text: Text) {
        val string = text.string
        if (RegexManager.matches(RegexKeys.LEAP_NUMBER_TITLE, string)) {
            initializeLeap()
        }
    }

    override fun onPlaySound(soundInstance: SoundInstance) {
        // detect and set overtime
        if (soundInstance.sound.identifier == CompanionSounds.MUSIC_OVERTIME_INTRO) {
            currentLeap.enteredOvertime = true
        }
    }

    override fun onGameMessage(text: Text, overlay: Boolean): Boolean {
        val string = text.string
        if (RegexManager.matches(RegexKeys.LEAP_STARTED, string)) {
            startLeap()
            return false
        } else {
            try {
                // check for leap completion
                val leapCompletionResult = RegexManager[RegexKeys.LEAP_COMPLETE]?.find(string)
                if (leapCompletionResult != null) {
                    val groupValues = leapCompletionResult.groupValues

                    // gather captures
                    val durationString = groupValues[1]

                    // process captures
                    currentLeap.duration = durationString
                    completeLeap()

                    return false
                }

                // check for leap end
                val leapEndedResult = RegexManager[RegexKeys.LEAP_ENDED]?.find(string)
                if (leapEndedResult != null) {
                    val groupValues = leapEndedResult.groupValues

                    // gather captures
                    val reasonString = groupValues[1]

                    // process captures
                    val endReason = Leap.EndReason.messageAssocation(reasonString)

                    currentLeap.endReason = endReason
                    finishLeap()

                    return false
                }

                // check for game end
                if (RegexManager.matches(RegexKeys.PLAYER_WON_SHOWDOWN, string)) {
                    placement = 1
                    endGame()

                    return false
                }

                val playerEliminatedResult = RegexManager[RegexKeys.PLAYER_ELIMINATED_PLACEMENT]?.find(string)
                if (playerEliminatedResult != null) {
                    val groupValues = playerEliminatedResult.groupValues

                    // gather captures
                    val placementString = groupValues[1]

                    // process captures
                    placement = placementString.toInt()
                    endGame()

                    return false
                }

                // check for game over
                if (RegexManager.matches(RegexKeys.GAME_OVER, string)) {
                    endGame()
                    return false
                }
            } catch (_: Throwable) {
            }

            currentLeap.onGameMessage(text)
        }

        return false
    }

    override fun onSectionUpdate(section: ParkourWarriorSection?, previousSection: ParkourWarriorSection?, medals: Int) {
        currentLeap.currentSection = section
    }

    override fun onRemove() {
        if (state != State.GAME_ENDED) {
            flushToJson(null)
        }
    }

    /**
     * Initializes the next leap.
     */
    private fun initializeLeap() {
        if (state == State.LEAP_INITIALIZED || state == State.LEAP_IN_PROGRESS) {
            return
        }

        // increment current leap
        currentLeapNumber++

        // create new leap
        currentLeap = Leap(currentLeapNumber)

        // set state
        state = State.LEAP_INITIALIZED
    }

    /**
     * Starts the current leap.
     */
    private fun startLeap() {
        if (state == State.LEAP_IN_PROGRESS) {
            return
        }

        // play music
        if (musicSettingSupplier()) {
            CompanionSoundManager.playMusic(GameTypes.PARKOUR_WARRIOR.settings.musicLoopSoundEvent, true)
        }

        // set state
        state = State.LEAP_IN_PROGRESS
    }

    /**
     * Completes the current leap.
     */
    private fun completeLeap() {
        if (state != State.LEAP_IN_PROGRESS) {
            return
        }

        // set state
        state = State.LEAP_COMPLETED
    }

    /**
     * Finishes the current leap.
     */
    private fun finishLeap() {
        if (state == State.FINISHED || state == State.GAME_ENDED) {
            return
        }

        // add that leap to storage
        allLeaps.add(currentLeap)

        // set state
        state = State.FINISHED
    }

    /**
     * Ends the game at the final leap.
     */
    private fun endGame() {
        if (state == State.GAME_ENDED) {
            return
        }

        // set state
        state = State.GAME_ENDED

        // flush to json
        flushToJson(placement)
    }

    private fun flushToJson(placement: Int?) {
        ParkourWarriorInstance.LOGGER.info("Flushing survivor instance to JSON: ${file.path}")

        val json = JsonObject()

        placement?.let { json.addProperty("placement", it) }

        val leapsJson = Leap.CODEC.listOf()
            .encodeStart(JsonOps.INSTANCE, allLeaps)
            .result()
            .orElseGet(::JsonArray)
        json.add("leaps", leapsJson)

        val gson = GsonBuilder().setPrettyPrinting().create()
        val jsonString = gson.toJson(json)
        file.parentFile.mkdirs()
        file.writeText(jsonString)
    }

    override fun renderDebugHud(textRendererConsumer: (Text) -> Unit) {
        textRendererConsumer(Text.literal("State: $state, Leap: $currentLeapNumber"))
        currentLeap.renderDebugHud(textRendererConsumer)
    }

    /**
     * The current state of the leap manager.
     */
    enum class State(val id: String) : StringIdentifiable {
        GAME_NOT_STARTED("game_not_started"),
        LEAP_INITIALIZED("leap_initialized"),
        LEAP_IN_PROGRESS("leap_in_progress"),
        LEAP_COMPLETED("leap_completed"),
        ELIMINATED("eliminated"),
        FINISHED("finished"),
        GAME_ENDED("game_ended");

        override fun asString(): String {
            return id
        }

        companion object {
            /**
             * The codec for this class.
             */
            val CODEC: Codec<State> = StringIdentifiable.createCodec(State::values)
        }
    }
}

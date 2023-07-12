package dev.andante.companion.game.instance.parkour_warrior.mode.survivor

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import dev.andante.companion.game.instance.parkour_warrior.ParkourWarriorInstance
import dev.andante.companion.game.instance.parkour_warrior.ParkourWarriorSection
import dev.andante.companion.game.instance.parkour_warrior.mode.ParkourWarriorModeInstance
import dev.andante.companion.game.type.GameTypes
import dev.andante.companion.helper.FileHelper.companionFile
import dev.andante.companion.regex.RegexKeys
import dev.andante.companion.regex.RegexManager
import dev.andante.companion.scoreboard.ScoreboardAccessor
import dev.andante.companion.setting.MusicSettings
import dev.andante.companion.sound.CompanionSoundManager
import dev.andante.companion.sound.CompanionSounds
import net.minecraft.client.MinecraftClient
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
    private var placement: Int? = null

    /**
     * The individual placements for each leap.
     */
    private var leapPlacements: List<String> = List(8) { "/" }

    /**
     * Stored previous leaps.
     */
    private val allLeaps: MutableList<Leap> = mutableListOf()

    override fun tick(client: MinecraftClient) {
        try {
            val firstRowString = ScoreboardAccessor.getSidebarRow(1)

            // check for placements
            val placementsResult = RegexManager[RegexKeys.PARKOUR_WARRIOR_SURVIVOR_PLACEMENTS_SIDEBAR]?.find(firstRowString)
            if (placementsResult != null) {
                val groupValues = placementsResult.groupValues
                if (groupValues.size - 1 == leapPlacements.size) {
                    val placementStrings = groupValues.subList(1, leapPlacements.size + 1)
                    leapPlacements = placementStrings
                }
            }
        } catch (_: Throwable) {
        }
    }

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
                    endGame(State.ELIMINATED)

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
        flushToJson()
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
        // play music
        if (musicSettingSupplier()) {
            CompanionSoundManager.playMusic(GameTypes.PARKOUR_WARRIOR.settings.musicLoopSoundEvent, true)
        }

        // set state
        state = State.LEAP_IN_PROGRESS

        // flush to json
        flushToJson()
    }

    /**
     * Completes the current leap.
     */
    private fun completeLeap() {
        if (state == State.LEAP_IN_PROGRESS) {
            return
        }

        // play music
        if (musicSettingSupplier()) {
            CompanionSoundManager.playMusic(GameTypes.PARKOUR_WARRIOR.settings.musicLoopSoundEvent, true)
        }

        // set state
        state = State.LEAP_COMPLETED

        // flush to json
        flushToJson()
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

        // flush to json
        flushToJson()
    }

    /**
     * Ends the game at the final leap.
     */
    private fun endGame(newState: State = State.GAME_ENDED) {
        if (state == newState || !(state == State.ELIMINATED || state == State.LEAP_IN_PROGRESS)) {
            return
        }

        // stop music
        CompanionSoundManager.stopMusic()

        // set state
        state = newState

        // flush to json
        flushToJson()
    }

    private fun flushToJson() {
        ParkourWarriorInstance.LOGGER.info("Flushing survivor instance to JSON: ${file.path}")

        val json = JsonObject()

        placement?.let { json.addProperty("placement", it) }

        val leapPlacementsJson = Codec.STRING.listOf()
            .encodeStart(JsonOps.INSTANCE, leapPlacements)
            .result()
            .orElseGet(::JsonArray)
        json.add("leap_placements", leapPlacementsJson)

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
        textRendererConsumer(Text.literal("Placements: ${leapPlacements.joinToString(separator = "] [", prefix = "[", postfix = "]")}"))
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

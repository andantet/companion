package dev.andante.companion.api.game.instance.parkour_warrior_dojo

import dev.andante.companion.api.game.instance.GameInstance
import dev.andante.companion.api.game.type.GameType
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import org.intellij.lang.annotations.RegExp

/**
 * An instance of Parkour Warrior Dojo.
 */
class ParkourWarriorDojoInstance(type: GameType<ParkourWarriorDojoInstance>) : GameInstance<ParkourWarriorDojoInstance>(type) {
    /**
     * The active mode.
     */
    private var mode = Mode.CHALLENGE

    /**
     * The active mode instance.
     */
    private var modeInstance: ParkourWarriorDojoModeInstance? = null

    override fun onGameMessage(text: Text, overlay: Boolean) {
        val string = text.string

        // check for chat mode
        val modeMatchResult = MODE_CHANGE_REGEX.find(string)
        if (modeMatchResult != null) {
            val modeString = modeMatchResult.groupValues[1]
            Mode.ofChatString(modeString)?.let { matchedMode ->
                if (matchedMode != mode) {
                    clearInstance()
                    onModeUpdate(matchedMode, mode)
                    mode = matchedMode
                }
            }

            return
        }

        // check for course restart
        if (string.matches(COURSE_RESTARTED_REGEX)) {
            if (modeInstance?.onCourseRestart() == true) {
                clearInstance()
                return
            }
        }

        // check for course finish
        if (string.matches(RUN_COMPLETE_REGEX) || string.matches(NEW_PERSONAL_RECORD_REGEX)) {
            clearInstance()
            return
        }
    }

    /**
     * Called when the mode is updated.
     */
    private fun onModeUpdate(mode: Mode, oldMode: Mode) {
        if (mode == Mode.PRACTICE) {
            setInstance(PracticeModeInstance())
        }
    }

    override fun onTitle(text: Text) {
        // detect challenge run start
        if (text.string == GO_TEXT) {
            setInstance(ChallengeModeInstance())
        }
    }

    override fun onSubtitle(text: Text) {
        modeInstance?.onSubtitle(text)
    }

    override fun renderDebugHud(textRendererConsumer: (Text) -> Unit) {
        textRendererConsumer(Text.literal(mode.name))
        modeInstance?.let { instance ->
            textRendererConsumer(Text.literal(instance.currentSection.toString()))
            instance.renderDebugHud(textRendererConsumer)
        }
    }

    /**
     * Sets the active game instance.
     */
    private fun setInstance(instance: ParkourWarriorDojoModeInstance?) {
        modeInstance?.onRemove()
        modeInstance = instance
        instance?.onInitialize()
    }

    /**
     * Clears the active mode instance.
     */
    private fun clearInstance() {
        setInstance(null)
    }

    companion object {
        /**
         * The title text displayed when a challenge mode run starts.
         */
        const val GO_TEXT = "Go!"

        /**
         * A regex that matches the message sent when the mode changes.
         */
        @RegExp
        val MODE_CHANGE_REGEX = Regex("\\[.] You are now in: (\\w+) Mode")

        /**
         * A regex that matches the message sent when the player restarts a course.
         */
        @RegExp
        val COURSE_RESTARTED_REGEX = Regex("\\[.] You restarted the course!")

        /**
         * A regex that matches the message sent when the player completes a run.
         */
        @RegExp
        val RUN_COMPLETE_REGEX = Regex("\\[.] Run complete!")

        /**
         * A regex that matches the message sent when the player sets a new personal record.
         */
        @get:RegExp
        val NEW_PERSONAL_RECORD_REGEX get() = Regex("\\[.] .. ${MinecraftClient.getInstance().session.profile.name} has set a new personal record! ")
    }

    /**
     * The active mode of the Parkour Warrior Dojo instance.
     */
    enum class Mode(
        /**
         * The string displayed in chat when this mode is switched to.
         */
        val chatString: String
    ) {
        /**
         * The challenge mode.
         */
        CHALLENGE("Challenge"),

        /**
         * The practice mode.
         */
        PRACTICE("Practice");

        companion object {
            /**
             * A map of all mode chat strings to their modes.
             */
            private val CHAT_STRING_TO_MODE = Mode.values().associateBy(Mode::chatString)

            /**
             * @return the mode of the given chat string
             */
            fun ofChatString(string: String): Mode? {
                return CHAT_STRING_TO_MODE[string]
            }
        }
    }
}

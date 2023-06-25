package dev.andante.companion.api.game.instance.parkour_warrior_dojo

import dev.andante.companion.api.game.instance.GameInstance
import dev.andante.companion.api.game.instance.parkour_warrior_dojo.mode.ChallengeModeInstance
import dev.andante.companion.api.game.instance.parkour_warrior_dojo.mode.DojoModeInstance
import dev.andante.companion.api.game.instance.parkour_warrior_dojo.mode.PracticeModeInstance
import dev.andante.companion.api.game.type.GameType
import dev.andante.companion.api.helper.AssociationHelper
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import org.intellij.lang.annotations.RegExp
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.UUID

/**
 * An instance of Parkour Warrior Dojo.
 */
class ParkourWarriorDojoInstance(type: GameType<ParkourWarriorDojoInstance>, uuid: UUID) : GameInstance<ParkourWarriorDojoInstance>(type, uuid) {
    /**
     * The active mode.
     */
    private var mode = Mode.CHALLENGE

    /**
     * The active mode instance.
     */
    private var modeInstance: DojoModeInstance? = null

    override fun tick(client: MinecraftClient) {
        modeInstance?.tick(client)
    }

    override fun onGameMessage(text: Text, overlay: Boolean) {
        val string = text.string

        // check for chat mode
        val modeMatchResult = MODE_CHANGE_REGEX.find(string)
        if (modeMatchResult != null) {
            val modeString = modeMatchResult.groupValues[1]
            Mode.chatStringAssociation(modeString)?.let { matchedMode ->
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
            setInstance(ChallengeModeInstance(MinecraftClient.getInstance().world!!))
        }
    }

    override fun onSubtitle(text: Text) {
        if (modeInstance?.onSubtitle(text) == true) {
            clearInstance()
        }
    }

    override fun afterRenderEntities(context: WorldRenderContext) {
        modeInstance?.afterRenderEntities(context)
    }

    override fun renderDebugHud(textRendererConsumer: (Text) -> Unit) {
        textRendererConsumer(Text.literal(mode.name))
        modeInstance?.renderDebugHud(textRendererConsumer)
    }

    /**
     * Sets the active game instance.
     */
    private fun setInstance(instance: DojoModeInstance?) {
        modeInstance?.let {
            LOGGER.info("Removing Parkour Warrior: Dojo mode instance ${it.uuid}")
            it.onRemove()
        }

        modeInstance = instance

        instance?.let {
            LOGGER.info("Initializing Parkour Warrior: Dojo mode instance ${it.uuid}")
            it.onInitialize()
        }
    }

    /**
     * Clears the active mode instance.
     */
    private fun clearInstance() {
        setInstance(null)
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger("[MCCI: Companion] Parkour Warrior Dojo Instance")

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
             * @return the mode of the given chat string
             */
            val chatStringAssociation = AssociationHelper.createAssociationFunction(Mode.values(), Mode::chatString)
        }
    }
}

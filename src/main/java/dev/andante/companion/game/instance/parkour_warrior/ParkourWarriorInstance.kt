package dev.andante.companion.game.instance.parkour_warrior

import dev.andante.companion.game.instance.GameInstance
import dev.andante.companion.game.instance.parkour_warrior.mode.ParkourWarriorModeInstance
import dev.andante.companion.game.instance.parkour_warrior.mode.dojo.PracticeModeInstance
import dev.andante.companion.game.instance.parkour_warrior.mode.dojo.challenge.ChallengeModeInstance
import dev.andante.companion.game.instance.parkour_warrior.mode.survivor.SurvivorModeInstance
import dev.andante.companion.game.type.GameType
import dev.andante.companion.helper.AssociationHelper
import dev.andante.companion.player.ghost.GhostPlayerManager
import dev.andante.companion.regex.RegexKeys
import dev.andante.companion.regex.RegexManager
import dev.andante.companion.scoreboard.ScoreboardAccessor
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.MinecraftClient
import net.minecraft.client.sound.SoundInstance
import net.minecraft.text.Text
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.UUID

/**
 * An instance of Parkour Warrior.
 */
class ParkourWarriorInstance(type: GameType<ParkourWarriorInstance>, uuid: UUID) : GameInstance<ParkourWarriorInstance>(type, uuid) {
    /**
     * The active mode.
     */
    private var mode = Mode.CHALLENGE
        set(value) {
            if (value != field) {
                clearInstance()
                onModeUpdate(value, field)
                field = value
            }
        }

    /**
     * The active mode instance.
     */
    private var modeInstance: ParkourWarriorModeInstance? = null

    override fun tick(client: MinecraftClient) {
        // check for survivor
        val firstRowString = ScoreboardAccessor.getSidebarRow(0)
        if (RegexManager.matches(RegexKeys.PARKOUR_WARRIOR_SURVIVOR_LEAP_SIDEBAR, firstRowString)) {
            mode = Mode.SURVIVOR
        }

        modeInstance?.tick(client)

        if (modeInstance != null) {
            GhostPlayerManager.tickTimeline(client)
        }
    }

    override fun onPlaySound(soundInstance: SoundInstance) {
        modeInstance?.onPlaySound(soundInstance)
    }

    override fun onGameMessage(text: Text, overlay: Boolean) {
        val string = text.string

        // check for chat mode
        val modeMatchResult = RegexManager[RegexKeys.PARKOUR_WARRIOR_DOJO_MODE_CHANGE]?.find(string)
        if (modeMatchResult != null) {
            val modeString = modeMatchResult.groupValues[1]
            Mode.chatStringAssociation(modeString)?.let { matchedMode ->
                mode = matchedMode
            }

            return
        }

        if (modeInstance?.onGameMessage(text, overlay) == true) {
            clearInstance()
            return
        }
    }

    /**
     * Called when the mode is updated.
     */
    private fun onModeUpdate(mode: Mode, oldMode: Mode) {
        when (mode) {
            Mode.PRACTICE -> setInstance(PracticeModeInstance())
            Mode.SURVIVOR -> setInstance(SurvivorModeInstance())
            else -> {}
        }
    }

    override fun onTitle(text: Text) {
        // detect challenge run start
        if (RegexManager.matches(RegexKeys.PARKOUR_WARRIOR_DOJO_COURSE_STARTED, text.string)) {
            setInstance(ChallengeModeInstance(MinecraftClient.getInstance().world!!))
        } else {
            modeInstance?.onTitle(text)
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
    private fun setInstance(instance: ParkourWarriorModeInstance?) {
        modeInstance?.let {
            LOGGER.info("Removing Parkour Warrior mode instance ${it.uuid}")
            it.onRemove()
        }

        modeInstance = instance

        instance?.let {
            LOGGER.info("Initializing Parkour Warrior mode instance ${it.uuid}")
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
        val LOGGER: Logger = LoggerFactory.getLogger("[MCCI: Companion] Parkour Warrior")
    }

    /**
     * The active mode of the Parkour Warrior instance.
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
        PRACTICE("Practice"),

        /**
         * The survivor mode.
         */
        SURVIVOR("Survivor");

        companion object {
            /**
             * @return the mode of the given chat string
             */
            val chatStringAssociation = AssociationHelper.createAssociationFunction(entries, Mode::chatString)
        }
    }
}

package dev.andante.companion.api.game.instance.parkour_warrior.mode

import dev.andante.companion.api.game.instance.parkour_warrior.ParkourWarriorSection
import dev.andante.companion.api.regex.RegexKeys
import dev.andante.companion.api.regex.RegexManager
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import java.util.UUID

/**
 * An instance of a Parkour Warrior Dojo mode.
 */
open class ParkourWarriorModeInstance(
    /**
     * Whether or not to play this mode's music.
     */
    val musicSettingSupplier: () -> Boolean
) {
    /**
     * The UUID of this mode instance.
     */
    val uuid: UUID = UUID.randomUUID()

    protected var currentSection: ParkourWarriorSection? = null

    /**
     * Called when the instance initializes.
     */
    open fun onInitialize() {
    }

    /**
     * Called every client tick.
     */
    open fun tick(client: MinecraftClient) {
    }

    /**
     * Called when the current parkour section updates.
     */
    open fun onSectionUpdate(section: ParkourWarriorSection?, previousSection: ParkourWarriorSection?, medals: Int) {
    }

    open fun afterRenderEntities(context: WorldRenderContext) {
    }

    /**
     * Called when the instance is cleared.
     */
    open fun onRemove() {
    }

    open fun renderDebugHud(textRendererConsumer: (Text) -> Unit) {
    }

    /**
     * @return whether to clear the game instance
     */
    open fun onSubtitle(text: Text): Boolean {
        val string = text.string

        try {
            // check for section
            val sectionMatchResult = RegexManager[RegexKeys.PARKOUR_WARRIOR_SECTION_TITLE]?.find(string)
            if (sectionMatchResult != null) {
                val groupValues = sectionMatchResult.groupValues

                // gather captures
                val branchString = groupValues[1]
                val branchNoString = groupValues[2]
                val sectionNoString = groupValues[3]
                val sectionNameString = groupValues[4]

                // parse captures
                val branch = ParkourWarriorSection.Branch.titleAbbreviationAssocation(branchString)
                val branchNo = branchNoString.toInt()
                val sectionNo = sectionNoString.toInt()

                if (branch != null) {
                    // update section
                    val section = ParkourWarriorSection(branch, branchNo, sectionNo, sectionNameString)
                    onSectionUpdate(section, currentSection, 0)
                    currentSection = section
                }

                return false
            }
        } catch (_: Throwable) {
        }

        // check for medal
        val medalsMatchResult = RegexManager[RegexKeys.PARKOUR_WARRIOR_DOJO_MEDAL_GAINED]?.find(string)
        if (medalsMatchResult != null) {
            val groupValues = medalsMatchResult.groupValues

            val medalsString = groupValues[1]
            val medals = medalsString.toInt()

            // update section
            onSectionUpdate(null, currentSection, medals)
            currentSection = null
        }

        return false
    }

    open fun onGameMessage(text: Text, overlay: Boolean): Boolean {
        return false
    }
}

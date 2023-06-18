package dev.andante.companion.api.game.instance.parkour_warrior_dojo

import dev.andante.companion.api.game.type.GameTypes
import dev.andante.companion.api.sound.CompanionSoundManager
import dev.andante.companion.api.sound.CompanionSounds
import net.minecraft.text.Text
import org.intellij.lang.annotations.RegExp

/**
 * An instance of a Parkour Warrior Dojo mode.
 */
open class ParkourWarriorDojoModeInstance {
    private var _currentSection: Section? = null

    /**
     * The current section that the player is present at.
     */
    val currentSection: Section? get() = _currentSection

    /**
     * Called when the instance initializes.
     */
    open fun onInitialize() {
        val settings = GameTypes.PARKOUR_WARRIOR_DOJO.settings
        if (settings.musicSettingSupplier()) {
            CompanionSoundManager.stop(CompanionSounds.MUSIC_GAME_PARKOUR_WARRIOR_LOOP_FADE_OUT)
            CompanionSoundManager.playMusic(settings.musicLoopSoundEvent)
        }
    }

    /**
     * Called when the current parkour section updates.
     */
    open fun onSectionUpdate(section: Section?, previousSection: Section?) {
    }

    /**
     * Called when a course is restarted by the player.
     * @return whether the mode instance should be cleared
     */
    open fun onCourseRestart(): Boolean {
        return false
    }

    /**
     * Called when the instance is cleared.
     */
    open fun onRemove() {
        if (GameTypes.PARKOUR_WARRIOR_DOJO.settings.musicSettingSupplier()) {
            CompanionSoundManager.playMusic(null)
            CompanionSoundManager.play(CompanionSounds.MUSIC_GAME_PARKOUR_WARRIOR_LOOP_FADE_OUT)
        }
    }

    open fun renderDebugHud(textRendererConsumer: (Text) -> Unit) {
    }

    fun onSubtitle(text: Text) {
        val string = text.string

        try {
            // check for section
            val sectionMatchResult = SECTION_ENTERED_REGEX.find(string)
            if (sectionMatchResult != null) {
                val groupValues = sectionMatchResult.groupValues

                // gather captures
                val branchString = groupValues[1]
                val branchNoString = groupValues[2]
                val sectionNoString = groupValues[3]
                val sectionNameString = groupValues[4]

                // parse captures
                val branch = Section.Branch.ofAbbreviation(branchString)
                val branchNo = branchNoString.toInt()
                val sectionNo = sectionNoString.toInt()

                if (branch != null) {
                    // update section
                    val section = Section(branch, branchNo, sectionNo, sectionNameString)
                    onSectionUpdate(section, _currentSection)
                    _currentSection = section
                }

                return
            }
        } catch (_: Throwable) {
        }

        // check for medal
        if (string.matches(MEDAL_GAINED_REGEX)) {
            // update section
            onSectionUpdate(null, _currentSection)
            _currentSection = null
        }
    }

    companion object {
        /**
         * A regex that matches the title sent when the player enters a section.
         */
        @RegExp
        val SECTION_ENTERED_REGEX = Regex("\\[([MB])([0-9]+)-([0-9]+)] (.+)")

        /**
         * A regex that matches the title sent when the player gains a medal.
         */
        @RegExp
        val MEDAL_GAINED_REGEX = Regex("\\+[0-9]+.")
    }

    /**
     * A store of section information.
     */
    data class Section(
        /**
         * The section's branch.
         */
        val branch: Branch,

        /**
         * The section's branch number.
         */
        val branchNumber: Int,

        /**
         * The individual section's number.
         */
        val sectionNumber: Int,

        /**
         * The individual section's name.
         */
        val sectionName: String
    ) {
        enum class Branch(
            /**
             * The abbreviated version displayed on the section title.
             */
            val titleAbbreviation: String
        ) {
            MAIN("M"),
            BONUS("B");

            companion object {
                /**
                 * A map of all branch title abbreviations to their branches.
                 */
                private val TITLE_ABBREVIATION_TO_BRANCH = Branch.values().associateBy(Branch::titleAbbreviation)

                /**
                 * @return the mode of the given chat string
                 */
                fun ofAbbreviation(string: String): Branch? {
                    return TITLE_ABBREVIATION_TO_BRANCH[string]
                }
            }
        }
    }
}

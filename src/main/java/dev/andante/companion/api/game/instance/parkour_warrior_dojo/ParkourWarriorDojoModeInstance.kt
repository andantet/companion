package dev.andante.companion.api.game.instance.parkour_warrior_dojo

import com.google.gson.JsonObject
import dev.andante.companion.api.game.type.GameTypes
import dev.andante.companion.api.helper.AssociationHelper
import dev.andante.companion.api.scoreboard.ScoreboardAccessor
import dev.andante.companion.api.sound.CompanionSoundManager
import dev.andante.companion.api.sound.CompanionSounds
import dev.andante.companion.setting.MusicSettings
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import org.intellij.lang.annotations.RegExp
import java.util.UUID

/**
 * An instance of a Parkour Warrior Dojo mode.
 */
open class ParkourWarriorDojoModeInstance(
    /**
     * Whether or not to play this mode's music.
     */
    val musicSettingSupplier: () -> Boolean
) {
    /**
     * The UUID of this mode instance.
     */
    val uuid: UUID = UUID.randomUUID()

    protected var currentSection: Section? = null
    protected var courseNumber: Int = -1

    /**
     * Called when the instance initializes.
     */
    open fun onInitialize() {
        if (musicSettingSupplier()) {
            CompanionSoundManager.stop(CompanionSounds.MUSIC_GAME_PARKOUR_WARRIOR_LOOP_FADE_OUT)
            CompanionSoundManager.playMusic(GameTypes.PARKOUR_WARRIOR_DOJO.settings.musicLoopSoundEvent)
        }
    }

    /**
     * Called every client tick.
     */
    open fun tick(client: MinecraftClient) {
        try {
            // check for course
            val firstRowString = ScoreboardAccessor.getSidebarRow(0)
            val courseMatchResult = COURSE_SIDEBAR_REGEX.find(firstRowString)
            if (courseMatchResult != null) {
                val groupValues = courseMatchResult.groupValues

                val courseString = groupValues[1]
                val course = courseString.toInt()

                courseNumber = course
            }
        } catch (_: Throwable) {
        }
    }

    /**
     * Called when the current parkour section updates.
     */
    open fun onSectionUpdate(section: Section?, previousSection: Section?, medals: Int) {
    }

    /**
     * Called when a course is completed by the player.
     */
    open fun onCourseCompleted(medals: Int, time: String, completionType: CompletionType) {
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
        if (CompanionSoundManager.stopMusic()) {
            CompanionSoundManager.play(CompanionSounds.MUSIC_GAME_PARKOUR_WARRIOR_LOOP_FADE_OUT) { MusicSettings.INSTANCE.musicVolume }
        }
    }

    open fun renderDebugHud(textRendererConsumer: (Text) -> Unit) {
        textRendererConsumer(Text.literal(currentSection?.toJson().toString()))
    }

    /**
     * @return whether to clear the game instance
     */
    fun onSubtitle(text: Text): Boolean {
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
                val branch = Section.Branch.titleAbbreviationAssocation(branchString)
                val branchNo = branchNoString.toInt()
                val sectionNo = sectionNoString.toInt()

                if (branch != null) {
                    // update section
                    val section = Section(branch, branchNo, sectionNo, sectionNameString)
                    onSectionUpdate(section, currentSection, 0)
                    currentSection = section
                }

                return false
            }

            // check for course finish
            val completionResult = RUN_COMPLETE_SUBTITLE_REGEX.find(string)
            if (completionResult != null) {
                val groupValues = completionResult.groupValues

                // gather captures
                val medalsString = groupValues[1]
                val timeString = groupValues[2]
                val completionTypeString = groupValues[3]

                val medals = medalsString.toInt()
                val completionType = CompletionType.valueOf(completionTypeString.uppercase())

                onCourseCompleted(medals, timeString, completionType)
                return true
            }
        } catch (_: Throwable) {
        }

        // check for medal
        val medalsMatchResult = MEDAL_GAINED_REGEX.find(string)
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
        val MEDAL_GAINED_REGEX = Regex("\\+([0-9]+).")

        /**
         * A regex that matches the subtitle sent when the player completes a run.
         */
        @RegExp
        val RUN_COMPLETE_SUBTITLE_REGEX = Regex(".([0-9]+) .([0-9:.]+) .(\\w+)")

        /**
         * A regex that matches the course text displayed on the sidebar.
         */
        @RegExp
        val COURSE_SIDEBAR_REGEX = Regex(".COURSE: Course #([0-9]+)")
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
        fun toJson(): JsonObject {
            val json = JsonObject()
            json.addProperty("branch", branch.name)
            json.addProperty("branch_number", branchNumber)
            json.addProperty("section_number", sectionNumber)
            json.addProperty("section_name", sectionName)
            return json
        }

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
                 * @return the mode of the given chat string
                 */
                val titleAbbreviationAssocation = AssociationHelper.createAssociationFunction(Branch.values(), Branch::titleAbbreviation)
            }
        }
    }

    enum class CompletionType {
        STANDARD,
        ADVANCED,
        EXPERT,
        INCOMPLETE
    }

    enum class Difficulty(
        val endingMedals: Int
    ) {
        EASY(1),
        MEDIUM(2),
        HARD(3);

        companion object {
            /**
             * @return the difficulty of the given ending medals
             */
            val endingMedalsAssociation = AssociationHelper.createAssociationFunction(Difficulty.values(), Difficulty::endingMedals)
        }
    }
}

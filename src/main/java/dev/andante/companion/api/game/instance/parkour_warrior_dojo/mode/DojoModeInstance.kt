package dev.andante.companion.api.game.instance.parkour_warrior_dojo.mode

import dev.andante.companion.api.game.instance.parkour_warrior_dojo.DojoChallengeRunManager
import dev.andante.companion.api.game.instance.parkour_warrior_dojo.DojoSection
import dev.andante.companion.api.game.instance.parkour_warrior_dojo.mode.challenge.DojoCompletionType
import dev.andante.companion.api.game.type.GameTypes
import dev.andante.companion.api.player.ghost.GhostPlayerManager
import dev.andante.companion.api.regex.RegexKeys
import dev.andante.companion.api.regex.RegexManager
import dev.andante.companion.api.scoreboard.ScoreboardAccessor
import dev.andante.companion.api.sound.CompanionSoundManager
import dev.andante.companion.api.sound.CompanionSounds
import dev.andante.companion.setting.MusicSettings
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import java.util.UUID

/**
 * An instance of a Parkour Warrior Dojo mode.
 */
open class DojoModeInstance(
    /**
     * Whether or not to play this mode's music.
     */
    val musicSettingSupplier: () -> Boolean
) {
    /**
     * The UUID of this mode instance.
     */
    val uuid: UUID = UUID.randomUUID()

    protected var currentSection: DojoSection? = null
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
            val courseMatchResult = RegexManager[RegexKeys.PARKOUR_WARRIOR_DOJO_COURSE_SIDEBAR]?.find(firstRowString)
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
    open fun onSectionUpdate(section: DojoSection?, previousSection: DojoSection?, medals: Int) {
    }

    /**
     * Called when a course is completed by the player.
     */
    open fun onCourseCompleted(medals: Int, time: String, completionType: DojoCompletionType) {
    }

    /**
     * Called when a course is restarted by the player.
     * @return whether the mode instance should be cleared
     */
    open fun onCourseRestart(): Boolean {
        return false
    }

    open fun afterRenderEntities(context: WorldRenderContext) {
    }

    /**
     * Called when the instance is cleared.
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun onRemove() {
        if (CompanionSoundManager.stopMusic()) {
            CompanionSoundManager.play(CompanionSounds.MUSIC_GAME_PARKOUR_WARRIOR_LOOP_FADE_OUT) { MusicSettings.INSTANCE.musicVolume }
        }

        GlobalScope.launch {
            synchronized(DojoChallengeRunManager) {
                DojoChallengeRunManager.reload()
            }
        }

        GhostPlayerManager.reset()
    }

    open fun renderDebugHud(textRendererConsumer: (Text) -> Unit) {
    }

    /**
     * @return whether to clear the game instance
     */
    fun onSubtitle(text: Text): Boolean {
        val string = text.string

        try {
            // check for section
            val sectionMatchResult = RegexManager[RegexKeys.PARKOUR_WARRIOR_DOJO_SECTION_TITLE]?.find(string)
            if (sectionMatchResult != null) {
                val groupValues = sectionMatchResult.groupValues

                // gather captures
                val branchString = groupValues[1]
                val branchNoString = groupValues[2]
                val sectionNoString = groupValues[3]
                val sectionNameString = groupValues[4]

                // parse captures
                val branch = DojoSection.Branch.titleAbbreviationAssocation(branchString)
                val branchNo = branchNoString.toInt()
                val sectionNo = sectionNoString.toInt()

                if (branch != null) {
                    // update section
                    val section = DojoSection(branch, branchNo, sectionNo, sectionNameString)
                    onSectionUpdate(section, currentSection, 0)
                    currentSection = section
                }

                return false
            }

            // check for course finish
            val completionResult = RegexManager[RegexKeys.PARKOUR_WARRIOR_DOJO_RUN_COMPLETE_TITLE]?.find(string)
            if (completionResult != null) {
                val groupValues = completionResult.groupValues

                // gather captures
                val medalsString = groupValues[1]
                val timeString = groupValues[2]
                val completionTypeString = groupValues[3]

                val medals = medalsString.toInt()
                val completionType = DojoCompletionType.valueOf(completionTypeString.uppercase())

                onCourseCompleted(medals, timeString, completionType)
                return true
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

}

package dev.andante.companion.api.game.instance.parkour_warrior.mode.dojo

import dev.andante.companion.api.game.instance.parkour_warrior.mode.ParkourWarriorModeInstance
import dev.andante.companion.api.game.instance.parkour_warrior.mode.dojo.challenge.DojoChallengeRunManager
import dev.andante.companion.api.game.instance.parkour_warrior.mode.dojo.challenge.DojoCompletionType
import dev.andante.companion.api.game.type.GameTypes
import dev.andante.companion.api.player.ghost.GhostPlayerManager
import dev.andante.companion.api.regex.RegexKeys
import dev.andante.companion.api.regex.RegexManager
import dev.andante.companion.api.scoreboard.ScoreboardAccessor
import dev.andante.companion.api.setting.MusicSettings
import dev.andante.companion.api.sound.CompanionSoundManager
import dev.andante.companion.api.sound.CompanionSounds
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text

open class DojoModeInstance(musicSettingSupplier: () -> Boolean) : ParkourWarriorModeInstance(musicSettingSupplier) {
    protected var courseNumber: Int = -1
    protected var dailyChallenge: Boolean = false

    override fun onInitialize() {
        if (musicSettingSupplier()) {
            CompanionSoundManager.stop(CompanionSounds.MUSIC_GAME_PARKOUR_WARRIOR_LOOP_FADE_OUT)
            CompanionSoundManager.playMusic(GameTypes.PARKOUR_WARRIOR.settings.musicLoopSoundEvent)
        }
    }

    override fun tick(client: MinecraftClient) {
        try {
            val firstRowString = ScoreboardAccessor.getSidebarRow(0)

            // check for course
            val courseMatchResult = RegexManager[RegexKeys.PARKOUR_WARRIOR_DOJO_COURSE_SIDEBAR]?.find(firstRowString)
            if (courseMatchResult != null) {
                val groupValues = courseMatchResult.groupValues

                val courseString = groupValues[1]
                val course = courseString.toInt()

                courseNumber = course
            }

            // check for daily challenge
            if (!dailyChallenge && RegexManager.matches(RegexKeys.PARKOUR_WARRIOR_DOJO_COURSE_SIDEBAR_DAILY_CHALLENGE, firstRowString)) {
                dailyChallenge = true
            }
        } catch (_: Throwable) {
        }
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

    override fun onGameMessage(text: Text, overlay: Boolean): Boolean {
        val string = text.string

        // check for course restart
        if (RegexManager.matches(RegexKeys.PARKOUR_WARRIOR_DOJO_COURSE_RESTARTED, string)) {
            if (onCourseRestart()) {
                return true
            }
        }

        return false
    }

    override fun onSubtitle(text: Text): Boolean {
        val string = text.string

        try {
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

        return super.onSubtitle(text)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onRemove() {
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
}

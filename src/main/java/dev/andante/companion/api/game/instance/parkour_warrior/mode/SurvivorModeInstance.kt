package dev.andante.companion.api.game.instance.parkour_warrior.mode

import dev.andante.companion.api.regex.RegexKeys
import dev.andante.companion.api.regex.RegexManager
import dev.andante.companion.api.scoreboard.ScoreboardAccessor
import dev.andante.companion.setting.MusicSettings
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text

class SurvivorModeInstance : ParkourWarriorModeInstance({ MusicSettings.INSTANCE.parkourWarriorSurvivorMusic }) {
    private var currentLeap: Int = 0
    private var maxLeaps: Int = 8

    override fun tick(client: MinecraftClient) {
        try {
            // check for leap
            val firstRowString = ScoreboardAccessor.getSidebarRow(0)
            val leapMatchResult = RegexManager[RegexKeys.PARKOUR_WARRIOR_SURVIVOR_LEAP_SIDEBAR]?.find(firstRowString)
            if (leapMatchResult != null) {
                val currentLeapString = leapMatchResult.groupValues[1]
                val maxLeapsString = leapMatchResult.groupValues[2]

                currentLeap = currentLeapString.toInt()
                maxLeaps = maxLeapsString.toInt()
            }
        } catch (_: Throwable) {
        }
    }

    override fun renderDebugHud(textRendererConsumer: (Text) -> Unit) {
        textRendererConsumer(Text.literal("$currentLeap/$maxLeaps"))
    }
}

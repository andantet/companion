package dev.andante.companion.api.scoreboard

import net.minecraft.client.MinecraftClient
import net.minecraft.scoreboard.Scoreboard
import net.minecraft.scoreboard.ScoreboardObjective

object ScoreboardAccessor {
    /**
     * The client instance.
     */
    private val CLIENT = MinecraftClient.getInstance()

    /**
     * The id of the sidebar display slot.
     */
    private val SIDEBAR_DISPLAY_SLOT_ID = Scoreboard.getDisplaySlotId("sidebar")

    private fun getScoreboard(client: MinecraftClient = CLIENT): Scoreboard? {
        return client.player?.scoreboard
    }

    fun getSidebarObjective(client: MinecraftClient = CLIENT): ScoreboardObjective? {
        return getScoreboard(client)?.getObjectiveForSlot(SIDEBAR_DISPLAY_SLOT_ID)
    }
}

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

    /**
     * Gets the client scoreboard.
     */
    private fun getScoreboard(): Scoreboard? {
        return CLIENT.player?.scoreboard
    }

    /**
     * Gets all the players assigned to the given objective.
     */
    private fun getPlayersForObjective(objective: ScoreboardObjective): Set<String> {
        val scoreboard = getScoreboard() ?: return emptySet()
        val playerObjectives = scoreboard.playerObjectives
        return playerObjectives.filterValues { it.containsKey(objective) }.keys
    }

    /**
     * Gets the objective assigned to the sidebar slot.
     */
    fun getSidebarObjective(): ScoreboardObjective? {
        return getScoreboard()?.getObjectiveForSlot(SIDEBAR_DISPLAY_SLOT_ID)
    }

    /**
     * Gets the given row from the sidebar objective.
     * @return the raw team name of the player at the index of [row]
     */
    fun getSidebarRow(row: Int): String {
        return try {
            val objective = getSidebarObjective()!!
            val players = getPlayersForObjective(objective).reversed()
            val player = players[row]
            val scoreboard = getScoreboard()!!
            val team = scoreboard.getPlayerTeam(player)!!
            team.prefix.string
        } catch (exception: NullPointerException) {
            ""
        } catch (exception: IndexOutOfBoundsException) {
            ""
        }
    }
}

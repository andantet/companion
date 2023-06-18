package dev.andante.companion.api.game.instance.tgttos

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import dev.andante.companion.api.game.round.Round
import dev.andante.companion.api.player.PlayerReference
import dev.andante.companion.api.text.TextRegex
import net.minecraft.text.Text
import org.intellij.lang.annotations.RegExp

class ToGetToTheOtherSideRound(roundNumber: Int) : Round(roundNumber) {
    private val finishedPlayers = mutableListOf<PlayerReference>()
    private var scoreEarned: Int = 0
    private var placement: Int = -1

    override fun onGameMessage(text: Text) {
        val string = text.string

        try {
            // check for finishes
            val finishedMatchResult = OTHER_PLAYER_FINISHED_REGEX.find(string) ?: PLAYER_FINISHED_REGEX.find(string)
            if (finishedMatchResult != null) {
                val playerNameString = finishedMatchResult.groupValues[1]
                val playerReference = PlayerReference.fromUsername(playerNameString)
                finishedPlayers.add(playerReference)
            }

            // check for score
            val scoreMatchResult = PLAYER_FINISHED_REGEX.find(string)
            if (scoreMatchResult != null) {
                val placementString = scoreMatchResult.groupValues[1]
                val scoreString = scoreMatchResult.groupValues[2]
                val placement = placementString.toInt()
                val score = scoreString.toInt()
                this.placement = placement
                scoreEarned = score
            }
        } catch (_: Throwable) {
        }
    }

    override fun renderDebugHud(textRendererConsumer: (Text) -> Unit) {
        textRendererConsumer(Text.literal("Finished players: ${finishedPlayers.size}"))
    }

    override fun toJson(json: JsonObject) {
        // round number
        json.addProperty("round", roundNumber)

        // placement
        json.addProperty("placement", placement)

        // earned score
        json.addProperty("score_earned", scoreEarned)

        // finished players
        val finishedPlayersJson = JsonArray()
        finishedPlayers.forEach { reference ->
            finishedPlayersJson.add(reference.toJson())
        }
        json.add("finished_players", finishedPlayersJson)
    }

    companion object {
        /**
         * The message sent when the player finishes in TGTTOS.
         */
        @RegExp
        val PLAYER_FINISHED_REGEX = Regex("\\[.] .. (${TextRegex.USERNAME_PATTERN}), you finished the round and came in ([0-9]+).. place! \\(Score: ([0-9]+).\\)")

        /**
         * The message sent when another player finishes in TGTTOS.
         */
        @RegExp
        val OTHER_PLAYER_FINISHED_REGEX = Regex("\\[.] .. (${TextRegex.USERNAME_PATTERN}) finished in [0-9]+..!")
    }
}

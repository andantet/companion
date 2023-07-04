package dev.andante.companion.api.game.instance.tgttos

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.mojang.serialization.JsonOps
import dev.andante.companion.api.extension.captureGroup
import dev.andante.companion.api.game.round.Round
import dev.andante.companion.api.helper.AssociationHelper
import dev.andante.companion.api.player.PlayerReference
import dev.andante.companion.api.regex.RegexKeys
import dev.andante.companion.api.regex.RegexManager
import dev.andante.companion.api.scoreboard.ScoreboardAccessor
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text

class ToGetToTheOtherSideRound(roundNumber: Int) : Round(roundNumber) {
    /**
     * The finished players of the round.
     */
    private val finishedPlayers = mutableListOf<PlayerReference>()

    /**
     * The score earned from the round.
     * Set when the player finishes.
     */
    private var scoreEarned: Int = 0

    /**
     * The player's placement.
     * Set when the player finishes.
     */
    private var placement: Int = -1

    /**
     * The map of the round.
     */
    private var map: GameMap? = null

    /**
     * The modifier of the round.
     */
    private var modifier: Modifier = Modifier.INACTIVE

    /**
     * An exposed reference to an object representing all game data for the round.
     */
    val data get() = Data(finishedPlayers.toList(), scoreEarned, placement, map, modifier)

    override fun tick(client: MinecraftClient) {
        // check for map
        if (map == null) {
            val firstRowString = ScoreboardAccessor.getSidebarRow(0)
            RegexManager[RegexKeys.MAP_SIDEBAR]?.captureGroup(firstRowString)?.let { mapString ->
                map = GameMap.sidebarNameAssocation(mapString)
            }
        }

        // check for modifier
        if (modifier == Modifier.INACTIVE) {
            val fourthRowString = ScoreboardAccessor.getSidebarRow(3)
            RegexManager[RegexKeys.MODIFIER_SIDEBAR]?.captureGroup(fourthRowString)?.let { modifierString ->
                modifier = Modifier.sidebarNameAssocation(modifierString) ?: Modifier.INACTIVE
            }
        }
    }

    override fun onGameMessage(text: Text) {
        val string = text.string

        // check for finishes
        val finishedMatchResult = RegexManager[RegexKeys.OTHER_PLAYER_FINISHED]?.find(string) ?: RegexManager[RegexKeys.PLAYER_FINISHED]?.find(string)
        if (finishedMatchResult != null) {
            val playerNameString = finishedMatchResult.groupValues[1]
            val playerReference = PlayerReference.fromUsername(playerNameString)
            finishedPlayers.add(playerReference)
        }

        // check for score
        val scoreMatchResult = RegexManager[RegexKeys.PLAYER_FINISHED]?.find(string)
        if (scoreMatchResult != null) {
            val placementString = scoreMatchResult.groupValues[1]
            val scoreString = scoreMatchResult.groupValues[2]
            val placement = placementString.toInt()
            val score = scoreString.toInt()
            this.placement = placement
            scoreEarned = score
        }
    }

    override fun renderDebugHud(textRendererConsumer: (Text) -> Unit) {
        textRendererConsumer(Text.literal("Map: $map"))
        textRendererConsumer(Text.literal("Modifier: $modifier"))
        textRendererConsumer(Text.literal("Placement: $placement"))
        textRendererConsumer(Text.literal("Score earned: $scoreEarned"))
        textRendererConsumer(Text.literal("Finished players: ${finishedPlayers.size}"))
    }

    override fun toJson(json: JsonObject) {
        // round number
        json.addProperty("round", roundNumber)

        // map
        map?.let { json.addProperty("map", it.name) }

        // modifier
        json.addProperty("modifier", modifier.name)

        // placement
        json.addProperty("placement", placement)

        // earned score
        json.addProperty("score_earned", scoreEarned)

        // finished players
        val finishedPlayersJson = PlayerReference.CODEC.listOf()
            .encodeStart(JsonOps.INSTANCE, finishedPlayers)
            .result()
            .orElse(JsonArray())
        json.add("finished_players", finishedPlayersJson)
    }

    enum class GameMap(
        /**
         * The name of this map as displayed on the side bar.
         */
        val sidebarName: String
    ) {
        BADLANDS("BADLANDS"),
        BASINS("BASINS"),
        BEEHIVE("BEEHIVE"),
        BOATS("BOATS"),
        BREAKDOWN("BREAKDOWN"),
        CLIFF("CLIFF"),
        DOORS("DOORS"),
        GLIDE("GLIDE"),
        INDUSTRY("INDUSTRY"),
        LAUNCHER("LAUNCHER"),
        PASSING("PASSING"),
        TRAIN_PASSING("TRAIN PASSING"),
        POLAR_PASSING("POLAR PASSING"),
        PIT("PIT"),
        PITS("PITS"),
        SHALLOW_LAVA("SHALLOW LAVA"),
        SKYDIVE("SKYDIVE"),
        SKYSCRAPER("SKYSCRAPER"),
        SIEGE("SIEGE"),
        SPIRAL_CLIMB("SPIRAL_CLIMB"),
        SPIRAL("SPIRAL"),
        TERRA_SWOOP_FORCE("TERRA SWOOP FORCE"),
        TO_THE_DOME("TO THE DOME!"),
        TRAIN("TRAIN"),
        AIR_TRAIN("AIR TRAIN"),
        SANTAS_TRAIN("SANTA'S TRAIN"),
        WALLS("WALLS"),
        TREETOP("TREETOP"),
        WATER_PARK("WATER PARK");

        companion object {
            /**
             * @return the map of the given sidebar name
             */
            val sidebarNameAssocation = AssociationHelper.createAssociationFunction(GameMap.values(), GameMap::sidebarName)
        }
    }

    /**
     * A To Get to the Other Side modifier.
     */
    enum class Modifier(
        /**
         * The name of this modifier as displayed on the side bar.
         */
        val sidebarName: String
    ) {
        RED_LIGHT_GREEN_LIGHT("RED LIGHT, GREEN LIGHT"),
        ONE_LIFE("ONE LIFE"),
        HOT_POTATO("HOT POTATO"),
        TNT_TIME("TNT TIME"),
        CRUMBLING_BLOCKS("CRUMBLING_BLOCKS"),
        EARLY_BIRDS("EARLY BIRDS"),
        SLAP_STICK("SLAP STICK"),
        CRACK_SHOT("CRACK SHOT"),
        DOUBLE_TIME("DOUBLE TIME"),
        INACTIVE("INACTIVE");

        companion object {
            /**
             * @return the modifier of the given sidebar name
             */
            val sidebarNameAssocation = AssociationHelper.createAssociationFunction(Modifier.values(), Modifier::sidebarName)
        }
    }

    /**
     * The game-relevant data of a round.
     */
    data class Data(
        /**
         * The players who finished a round.
         */
        val finishedPlayers: List<PlayerReference>,

        /**
         * The score earned from a round.
         */
        val scoreEarned: Int,

        /**
         * The placement of the player in a round.
         */
        val placement: Int,

        /**
         * The map of a round.
         */
        val map: GameMap?,

        /**
         * The modifier of a round.
         */
        val modifier: Modifier
    )
}

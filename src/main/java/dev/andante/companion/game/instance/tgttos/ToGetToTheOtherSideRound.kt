package dev.andante.companion.game.instance.tgttos

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.andante.companion.extension.captureGroup
import dev.andante.companion.extension.nullableFieldOf
import dev.andante.companion.game.round.Round
import dev.andante.companion.helper.AssociationHelper
import dev.andante.companion.player.PlayerReference
import dev.andante.companion.regex.RegexKeys
import dev.andante.companion.regex.RegexManager
import dev.andante.companion.scoreboard.ScoreboardAccessor
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import net.minecraft.util.StringIdentifiable

class ToGetToTheOtherSideRound(
    roundNumber: Int,

    /**
     * The finished players of the round.
     */
    private val finishedPlayers: MutableList<PlayerReference> = mutableListOf(),

    /**
     * The score earned from the round.
     * Set when the player finishes.
     */
    private var scoreEarned: Int = 0,

    /**
     * The player's placement.
     * Set when the player finishes.
     */
    private var placement: Int = -1,

    /**
     * The map of the round.
     */
    private var map: RoundMap? = null,

    /**
     * The modifier of the round.
     */
    private var modifier: RoundModifier = RoundModifier.INACTIVE
) : Round(roundNumber) {

    /**
     * An exposed reference to an object representing all game data for the round.
     */
    val data get() = Data(finishedPlayers.toList(), scoreEarned, placement, map, modifier)

    @Suppress("UNCHECKED_CAST")
    override fun <R : Round> getCodec(): Codec<in R> {
        return CODEC as Codec<in R>
    }

    override fun tick(client: MinecraftClient) {
        // check for map
        if (map == null) {
            val firstRowString = ScoreboardAccessor.getSidebarRow(0)
            RegexManager[RegexKeys.MAP_SIDEBAR]?.captureGroup(firstRowString)?.let { mapString ->
                map = RoundMap.sidebarNameAssocation(mapString)
            }
        }

        // check for modifier
        if (modifier == RoundModifier.INACTIVE) {
            val fourthRowString = ScoreboardAccessor.getSidebarRow(3)
            RegexManager[RegexKeys.MODIFIER_SIDEBAR]?.captureGroup(fourthRowString)?.let { modifierString ->
                modifier = RoundModifier.sidebarNameAssocation(modifierString) ?: RoundModifier.INACTIVE
            }
        }
    }

    override fun onGameMessage(text: Text) {
        val string = text.string

        // check for score
        val scoreMatchResult = RegexManager[RegexKeys.PLAYER_FINISHED]?.find(string)
        if (scoreMatchResult != null) {
            val placementString = scoreMatchResult.groupValues[1]
            val scoreString = scoreMatchResult.groupValues[2]

            val placement = placementString.toInt()
            val score = scoreString.toInt()

            this.placement = placement
            scoreEarned = score

            val profile = MinecraftClient.getInstance().session.profile
            val reference = PlayerReference(profile.id, profile.name)
            finishedPlayers.add(reference)

            return
        }

        // check for finishes
        val finishedMatchResult = RegexManager[RegexKeys.OTHER_PLAYER_FINISHED]?.find(string) ?: RegexManager[RegexKeys.PLAYER_FINISHED]?.find(string)
        if (finishedMatchResult != null) {
            val playerNameString = finishedMatchResult.groupValues[1]
            val playerReference = PlayerReference.fromUsername(playerNameString)
            finishedPlayers.add(playerReference)
            return
        }
    }

    override fun renderDebugHud(textRendererConsumer: (Text) -> Unit) {
        textRendererConsumer(Text.literal("Map: $map"))
        textRendererConsumer(Text.literal("Modifier: $modifier"))
        textRendererConsumer(Text.literal("Placement: $placement"))
        textRendererConsumer(Text.literal("Score earned: $scoreEarned"))
        textRendererConsumer(Text.literal("Finished players: ${finishedPlayers.size}"))
    }

    enum class RoundMap(
        val id: String,

        /**
         * The name of this map as displayed on the side bar.
         */
        val sidebarName: String
    ) : StringIdentifiable {
        BADLANDS("badlands", "BADLANDS"),
        BASINS("basins", "BASINS"),
        BEEHIVE("beehive", "BEEHIVE"),
        BOATS("boats", "BOATS"),
        BREAKDOWN("breakdown", "BREAKDOWN"),
        CLIFF("cliff", "CLIFF"),
        DOORS("doors", "DOORS"),
        GLIDE("glide", "GLIDE"),
        INDUSTRY("industry", "INDUSTRY"),
        LAUNCHER("launcher", "LAUNCHER"),
        PASSING("passing", "PASSING"),
        TRAIN_PASSING("train_passing", "TRAIN PASSING"),
        POLAR_PASSING("polar_passing", "POLAR PASSING"),
        PIT("pit", "PIT"),
        PITS("pits", "PITS"),
        SHALLOW_LAVA("shallow_lava", "SHALLOW LAVA"),
        SKYDIVE("skydive", "SKYDIVE"),
        SKYSCRAPER("skyscraper", "SKYSCRAPER"),
        SIEGE("siege", "SIEGE"),
        SPIRAL_CLIMB("spiral_climb", "SPIRAL_CLIMB"),
        SPIRAL("spiral", "SPIRAL"),
        TERRA_SWOOP_FORCE("terra_swoop_force", "TERRA SWOOP FORCE"),
        TO_THE_DOME("to_the_dome", "TO THE DOME!"),
        TRAIN("train", "TRAIN"),
        AIR_TRAIN("air_train", "AIR TRAIN"),
        SANTAS_TRAIN("santas_train", "SANTA'S TRAIN"),
        WALLS("walls", "WALLS"),
        TREETOP("treetop", "TREETOP"),
        WATER_PARK("water_park", "WATER PARK");

        override fun asString(): String {
            return id
        }

        companion object {
            /**
             * The codec of this class.
             */
            val CODEC: Codec<RoundMap> = StringIdentifiable.createCodec(RoundMap::values)

            /**
             * @return the map of the given sidebar name
             */
            val sidebarNameAssocation = AssociationHelper.createAssociationFunction(entries, RoundMap::sidebarName)
        }
    }

    /**
     * A To Get to the Other Side modifier.
     */
    enum class RoundModifier(
        val id: String,

        /**
         * The name of this modifier as displayed on the side bar.
         */
        val sidebarName: String
    ) : StringIdentifiable {
        RED_LIGHT_GREEN_LIGHT("red_light_green_light", "RED LIGHT, GREEN LIGHT"),
        ONE_LIFE("one_life", "ONE LIFE"),
        HOT_POTATO("hot_potato", "HOT POTATO"),
        TNT_TIME("tnt_time", "TNT TIME"),
        CRUMBLING_BLOCKS("crumbling_blocks", "CRUMBLING_BLOCKS"),
        EARLY_BIRDS("early_birds", "EARLY BIRDS"),
        SLAP_STICK("slap_stick", "SLAP STICK"),
        CRACK_SHOT("crack_shot", "CRACK SHOT"),
        DOUBLE_TIME("double_time", "DOUBLE TIME"),
        INACTIVE("inactive", "INACTIVE");

        override fun asString(): String {
            return id
        }

        companion object {
            val CODEC: Codec<RoundModifier> = StringIdentifiable.createCodec(RoundModifier::values)

            /**
             * @return the modifier of the given sidebar name
             */
            val sidebarNameAssocation = AssociationHelper.createAssociationFunction(entries, RoundModifier::sidebarName)
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
        val map: RoundMap?,

        /**
         * The modifier of a round.
         */
        val modifier: RoundModifier
    )

    companion object {
        /**
         * The codec of this round.
         */
        val CODEC: Codec<ToGetToTheOtherSideRound> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.INT.fieldOf("round_number")
                    .forGetter(ToGetToTheOtherSideRound::roundNumber),
                PlayerReference.CODEC.listOf().fieldOf("finished_players")
                    .forGetter(ToGetToTheOtherSideRound::finishedPlayers),
                Codec.INT.fieldOf("score_earned")
                    .forGetter(ToGetToTheOtherSideRound::scoreEarned),
                Codec.INT.fieldOf("placement")
                    .forGetter(ToGetToTheOtherSideRound::placement),
                RoundMap.CODEC.nullableFieldOf("map")
                    .forGetter(ToGetToTheOtherSideRound::map),
                RoundModifier.CODEC.fieldOf("modifier")
                    .forGetter(ToGetToTheOtherSideRound::modifier)
            ).apply(instance, ::ToGetToTheOtherSideRound)
        }
    }
}

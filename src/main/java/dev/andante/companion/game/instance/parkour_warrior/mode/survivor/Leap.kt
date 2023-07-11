package dev.andante.companion.game.instance.parkour_warrior.mode.survivor

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.andante.companion.extension.nullableFieldOf
import dev.andante.companion.game.instance.parkour_warrior.ParkourWarriorSection
import dev.andante.companion.helper.AssociationHelper
import dev.andante.companion.regex.RegexKeys
import dev.andante.companion.regex.RegexManager
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import net.minecraft.util.StringIdentifiable

/**
 * An instance of a leap in Parkour Warrior: Survivor.
 */
data class Leap(
    /**
     * The number of this leap.
     */
    val leapNumber: Int,

    /**
     * All completed sections of this leap.
     */
    val completedSections: MutableList<CompletedLeapSection> = mutableListOf(),

    /**
     * The runner's placement in this leap.
     */
    var placement: Int? = null,

    /**
     * The runner's time to completion.
     */
    var duration: String? = null,

    /**
     * The reason for the leap ending.
     */
    var endReason: EndReason? = null,

    /**
     * Whether this leap entered overtime.
     */
    var enteredOvertime: Boolean = false
) {
    /**
     * The current leap section.
     */
    var currentSection: ParkourWarriorSection? = null

    fun onGameMessage(text: Text) {
        val string = text.string

        // check for leap section completion
        if (RegexManager.matches(RegexKeys.LEAP_SECTION_COMPLETE, string)) {
            completeSection()
            return
        }
    }

    /**
     * Completes the current section.
     */
    private fun completeSection() {
        currentSection?.let { section ->
            val client = MinecraftClient.getInstance()
            val player = client.player
            val healthRemaining = player?.health ?: 0.0f
            val completedLeapSection = CompletedLeapSection(section, healthRemaining)
            completedSections.add(completedLeapSection)

            currentSection = null
        }
    }

    fun renderDebugHud(textRendererConsumer: (Text) -> Unit) {
        textRendererConsumer(Text.literal("Completed sections: ${completedSections.size}"))

        if (enteredOvertime) {
            textRendererConsumer(Text.literal("Last leap entered overtime"))
        }
    }

    enum class EndReason (
        val id: String,

        /**
         * The message displayed on leap end.
         */
        val message: String
    ) : StringIdentifiable {
        TIME_RAN_OUT("time_ran_out", "Time ran out"),
        MAX_PLAYERS_QUALIFIED("max_players_qualified", "Max players qualified");

        override fun asString(): String {
            return id
        }

        companion object {
            /**
             * The codec of this class.
             */
            val CODEC: Codec<EndReason> = StringIdentifiable.createCodec(EndReason::values)

            /**
             * @return the reason of the given chat string
             */
            val messageAssocation = AssociationHelper.createAssociationFunction(entries, EndReason::message)
        }
    }

    companion object {
        /**
         * The codec of this class.
         */
        val CODEC: Codec<Leap> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.INT.fieldOf("leap_number").forGetter(Leap::leapNumber),
                CompletedLeapSection.CODEC.listOf().fieldOf("completed_sections").forGetter(Leap::completedSections),
                Codec.INT.nullableFieldOf("placement").forGetter(Leap::placement),
                Codec.STRING.nullableFieldOf("duration").forGetter(Leap::duration),
                EndReason.CODEC.nullableFieldOf("end_reason").forGetter(Leap::endReason),
                Codec.BOOL.fieldOf("entered_overtime").forGetter(Leap::enteredOvertime)
            ).apply(instance, ::Leap)
        }
    }
}

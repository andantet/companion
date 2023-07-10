package dev.andante.companion.api.game.instance.parkour_warrior.mode.survivor

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.andante.companion.api.game.instance.parkour_warrior.ParkourWarriorSection

/**
 * A completed section of Parkour Warrior: Survivor mode.
 */
data class CompletedLeapSection(
    val section: ParkourWarriorSection,
    val healthRemaining: Float
) {
    companion object {
        /**
         * The codec for this class.
         */
        val CODEC: Codec<CompletedLeapSection> = RecordCodecBuilder.create { instance ->
            instance.group(
                ParkourWarriorSection.CODEC.fieldOf("section").forGetter(CompletedLeapSection::section),
                Codec.FLOAT.fieldOf("health_remaining").forGetter(CompletedLeapSection::healthRemaining)
            ).apply(instance, ::CompletedLeapSection)
        }
    }
}

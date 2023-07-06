package dev.andante.companion.api.game.instance.parkour_warrior.mode.dojo.challenge

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.andante.companion.api.game.instance.parkour_warrior.ParkourWarriorSection

/**
 * A completed section of Parkour Warrior: Dojo challenge mode.
 */
data class CompletedSection(
    val timestampMs: Long,
    val section: ParkourWarriorSection
) {
    companion object {
        /**
         * The codec for this class.
         */
        val CODEC: Codec<CompletedSection> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.LONG.fieldOf("timestamp_ms").forGetter(CompletedSection::timestampMs),
                ParkourWarriorSection.CODEC.fieldOf("section").forGetter(CompletedSection::section)
            ).apply(instance, ::CompletedSection)
        }
    }
}

package dev.andante.companion.api.setting

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

/**
 * Settings regarding metrics.
 */
data class MetricsSettings(
    /**
     * Whether to save Hole in the Wall metrics.
     */
    val holeInTheWallMetrics: Boolean,

    /**
     * Whether to save To Get to the Other Side metrics.
     */
    val toGetToTheOtherSideMetrics: Boolean,

    /**
     * Whether to save Sky Battle metrics.
     */
    val skyBattleMetrics: Boolean,

    /**
     * Whether to save Battle Box metrics.
     */
    val battleBoxMetrics: Boolean,

    /**
     * Whether to save Parkour Warrior: Dojo metrics.
     */
    val parkourWarriorMetrics: Boolean
) {
    companion object {
        /**
         * The default settings.
         */
        private val DEFAULT = MetricsSettings(
            holeInTheWallMetrics = true,
            toGetToTheOtherSideMetrics = true,
            skyBattleMetrics = true,
            battleBoxMetrics = true,
            parkourWarriorMetrics = true
        )

        /**
         * The codec for serializing these settings.
         */
        val CODEC: Codec<MetricsSettings> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.BOOL.fieldOf("hole_in_the_wall_metrics")
                    .orElse(DEFAULT.holeInTheWallMetrics)
                    .forGetter(MetricsSettings::holeInTheWallMetrics),
                Codec.BOOL.fieldOf("to_get_to_the_other_side_metrics")
                    .orElse(DEFAULT.toGetToTheOtherSideMetrics)
                    .forGetter(MetricsSettings::toGetToTheOtherSideMetrics),
                Codec.BOOL.fieldOf("sky_battle_metrics")
                    .orElse(DEFAULT.skyBattleMetrics)
                    .forGetter(MetricsSettings::skyBattleMetrics),
                Codec.BOOL.fieldOf("battle_box_metrics")
                    .orElse(DEFAULT.battleBoxMetrics)
                    .forGetter(MetricsSettings::battleBoxMetrics),
                Codec.BOOL.fieldOf("parkour_warrior_metrics")
                    .orElse(DEFAULT.parkourWarriorMetrics)
                    .forGetter(MetricsSettings::parkourWarriorMetrics)
            ).apply(instance, ::MetricsSettings)
        }

        /**
         * An container for these settings.
         */
        val CONTAINER = SettingsContainer("metrics", CODEC, DEFAULT)

        /**
         * The instance of these settings.
         */
        val INSTANCE get() = CONTAINER.serializableObject
    }
}

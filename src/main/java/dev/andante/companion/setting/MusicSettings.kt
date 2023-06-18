package dev.andante.companion.setting

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

/**
 * Settings regarding music.
 */
data class MusicSettings(
    /**
     * The volume of MCC: Island music.
     */
    val musicVolume: Float,

    /**
     * Whether to play Hole in the Wall music.
     */
    val holeInTheWallMusic: Boolean,

    /**
     * Whether to play To Get to the Other Side music.
     */
    val toGetToTheOtherSideMusic: Boolean,

    /**
     * Whether to play Sky Battle music.
     */
    val skyBattleMusic: Boolean,

    /**
     * Whether to play Battle Box music.
     */
    val battleBoxMusic: Boolean,

    /**
     * Whether to play Parkour Warrior: Dojo music.
     */
    val parkourWarriorDojoMusic: Boolean
) {
    companion object {
        /**
         * The default settings.
         */
        private val DEFAULT = MusicSettings(
            musicVolume = 0.25f,
            holeInTheWallMusic = true,
            toGetToTheOtherSideMusic = true,
            skyBattleMusic = true,
            battleBoxMusic = true,
            parkourWarriorDojoMusic = true
        )

        /**
         * The codec for serializing these settings.
         */
        val CODEC: Codec<MusicSettings> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.FLOAT.fieldOf("music_volume")
                    .orElse(DEFAULT.musicVolume)
                    .forGetter(MusicSettings::musicVolume),
                Codec.BOOL.fieldOf("hole_in_the_wall_music")
                    .orElse(DEFAULT.holeInTheWallMusic)
                    .forGetter(MusicSettings::holeInTheWallMusic),
                Codec.BOOL.fieldOf("to_get_to_the_other_side_music")
                    .orElse(DEFAULT.toGetToTheOtherSideMusic)
                    .forGetter(MusicSettings::toGetToTheOtherSideMusic),
                Codec.BOOL.fieldOf("sky_battle_music")
                    .orElse(DEFAULT.skyBattleMusic)
                    .forGetter(MusicSettings::skyBattleMusic),
                Codec.BOOL.fieldOf("battle_box_music")
                    .orElse(DEFAULT.battleBoxMusic)
                    .forGetter(MusicSettings::battleBoxMusic),
                Codec.BOOL.fieldOf("parkour_warrior_dojo_music")
                    .orElse(DEFAULT.parkourWarriorDojoMusic)
                    .forGetter(MusicSettings::parkourWarriorDojoMusic)
            ).apply(instance, ::MusicSettings)
        }

        /**
         * An container for these settings.
         */
        val CONTAINER = SettingsContainer("music", CODEC, DEFAULT)

        /**
         * The instance of these settings.
         */
        val INSTANCE get() = CONTAINER.serializableObject
    }
}

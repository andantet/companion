package dev.andante.companion.api.setting

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
     * Whether to play 'One Minute to MCC'/'Decision Dome' when the map 'To The Dome!' is played.
     */
    val toGetToTheOtherSideMapToTheDomeMusic: Boolean,

    /**
     * Whether to play the music faster when the modifier 'Double Time' is being played.
     */
    val toGetToTheOtherSideModifierDoubleTimeMusic: Boolean,

    /**
     * Whether to play Sky Battle music.
     */
    val skyBattleMusic: Boolean,

    /**
     * Whether to play Battle Box music.
     */
    val battleBoxMusic: Boolean,

    /**
     * Whether to play Parkour Warrior: Dojo music in Challenge Mode.
     */
    val parkourWarriorDojoChallengeModeMusic: Boolean,

    /**
     * Whether to play Parkour Warrior: Dojo music in Practice Mode.
     */
    val parkourWarriorDojoPracticeModeMusic: Boolean,

    /**
     * Whether to play Parkour Warrior: Survivor music.
     */
    val parkourWarriorSurvivorMusic: Boolean,
) {
    companion object {
        /**
         * The default settings.
         */
        private val DEFAULT = MusicSettings(
            musicVolume = 0.25f,
            holeInTheWallMusic = true,
            toGetToTheOtherSideMusic = true,
            toGetToTheOtherSideMapToTheDomeMusic = true,
            toGetToTheOtherSideModifierDoubleTimeMusic = false,
            skyBattleMusic = true,
            battleBoxMusic = true,
            parkourWarriorDojoChallengeModeMusic = true,
            parkourWarriorDojoPracticeModeMusic = true,
            parkourWarriorSurvivorMusic = true
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
                Codec.BOOL.fieldOf("to_get_to_the_other_side_map_to_the_dome_music")
                    .orElse(DEFAULT.toGetToTheOtherSideMapToTheDomeMusic)
                    .forGetter(MusicSettings::toGetToTheOtherSideMapToTheDomeMusic),
                Codec.BOOL.fieldOf("to_get_to_the_other_side_modifier_double_time_music")
                    .orElse(DEFAULT.toGetToTheOtherSideModifierDoubleTimeMusic)
                    .forGetter(MusicSettings::toGetToTheOtherSideModifierDoubleTimeMusic),
                Codec.BOOL.fieldOf("sky_battle_music")
                    .orElse(DEFAULT.skyBattleMusic)
                    .forGetter(MusicSettings::skyBattleMusic),
                Codec.BOOL.fieldOf("battle_box_music")
                    .orElse(DEFAULT.battleBoxMusic)
                    .forGetter(MusicSettings::battleBoxMusic),
                Codec.BOOL.fieldOf("parkour_warrior_dojo_challenge_mode_music")
                    .orElse(DEFAULT.parkourWarriorDojoChallengeModeMusic)
                    .forGetter(MusicSettings::parkourWarriorDojoChallengeModeMusic),
                Codec.BOOL.fieldOf("parkour_warrior_dojo_practice_mode_music")
                    .orElse(DEFAULT.parkourWarriorDojoPracticeModeMusic)
                    .forGetter(MusicSettings::parkourWarriorDojoPracticeModeMusic),
                Codec.BOOL.fieldOf("parkour_warrior_survivor_music")
                    .orElse(DEFAULT.parkourWarriorSurvivorMusic)
                    .forGetter(MusicSettings::parkourWarriorSurvivorMusic)
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

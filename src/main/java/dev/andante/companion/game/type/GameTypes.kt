package dev.andante.companion.game.type

import dev.andante.companion.game.instance.battle_box.BattleBoxInstance
import dev.andante.companion.game.instance.hitw.HoleInTheWallInstance
import dev.andante.companion.game.instance.parkour_warrior.ParkourWarriorInstance
import dev.andante.companion.game.instance.sky_battle.SkyBattleInstance
import dev.andante.companion.game.instance.tgttos.ToGetToTheOtherSideInstance
import dev.andante.companion.registry.BasicRegistry
import dev.andante.companion.setting.MetricsSettings
import dev.andante.companion.setting.MusicSettings
import dev.andante.companion.sound.CompanionSounds

/**
 * All registered game types.
 */
@Suppress("unused")
object GameTypes : BasicRegistry<GameType<*>>() {
    /**
     * Represents the game 'Hole in the Wall'.
     */
    val HOLE_IN_THE_WALL = register("hole_in_the_wall", GameType(::HoleInTheWallInstance, GameTypeSettings(
        "HOLE IN THE WALL", CompanionSounds.MUSIC_GAME_HOLE_IN_THE_WALL_LOOP,
        { MusicSettings.INSTANCE.holeInTheWallMusic }, { MetricsSettings.INSTANCE.holeInTheWallMetrics }
    )))

    /**
     * Represents the game 'To Get to the Other Side'.
     */
    val TO_GET_TO_THE_OTHER_SIDE = register("to_get_to_the_other_side", GameType(::ToGetToTheOtherSideInstance, GameTypeSettings(
        "TGTTOS", CompanionSounds.MUSIC_GAME_TO_GET_TO_THE_OTHER_SIDE_LOOP,
        { MusicSettings.INSTANCE.toGetToTheOtherSideMusic }, { MetricsSettings.INSTANCE.toGetToTheOtherSideMetrics }
    )))

    /**
     * Represents the game 'Sky Battle'.
     */
    val SKY_BATTLE = register("sky_battle", GameType(::SkyBattleInstance, GameTypeSettings(
        "SKY BATTLE", CompanionSounds.MUSIC_GAME_SKY_BATTLE_LOOP,
        { MusicSettings.INSTANCE.skyBattleMusic }, { MetricsSettings.INSTANCE.skyBattleMetrics }
    )))

    /**
     * Represents the game 'Battle Box'.
     */
    val BATTLE_BOX = register("battle_box", GameType(::BattleBoxInstance, GameTypeSettings(
        "BATTLE BOX", CompanionSounds.MUSIC_GAME_BATTLE_BOX_LOOP,
        { MusicSettings.INSTANCE.battleBoxMusic }, { MetricsSettings.INSTANCE.battleBoxMetrics }
    )))

    /**
     * Represents the game 'Parkour Warrior'.
     */
    val PARKOUR_WARRIOR = register("parkour_warrior", GameType(::ParkourWarriorInstance, GameTypeSettings(
        "PARKOUR WARRIOR", CompanionSounds.MUSIC_GAME_PARKOUR_WARRIOR_LOOP,
        { false }, { MetricsSettings.INSTANCE.parkourWarriorMetrics }
    )))
}

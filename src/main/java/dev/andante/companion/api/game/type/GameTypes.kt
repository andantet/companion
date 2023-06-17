package dev.andante.companion.api.game.type

import dev.andante.companion.api.game.instance.battle_box.BattleBoxInstance
import dev.andante.companion.api.game.instance.hitw.HoleInTheWallInstance
import dev.andante.companion.api.game.instance.parkour_warrior_dojo.ParkourWarriorDojoInstance
import dev.andante.companion.api.game.instance.sky_battle.SkyBattleInstance
import dev.andante.companion.api.game.instance.tgttos.ToGetToTheOtherSideInstance
import dev.andante.companion.api.registry.BasicRegistry
import dev.andante.companion.api.sound.CompanionSounds

/**
 * All registered game types.
 */
object GameTypes : BasicRegistry<GameType<*>>() {
    /**
     * Represents the game 'Hole in the Wall'.
     */
    val HOLE_IN_THE_WALL = register("hole_in_the_wall", GameType(::HoleInTheWallInstance, GameTypeSettings(
        "HOLE IN THE WALL", CompanionSounds.MUSIC_GAME_HOLE_IN_THE_WALL_LOOP
    )))

    /**
     * Represents the game 'To Get to the Other Side'.
     */
    val TO_GET_TO_THE_OTHER_SIDE = register("to_get_to_the_other_side", GameType(::ToGetToTheOtherSideInstance, GameTypeSettings(
        "TGTTOS", CompanionSounds.MUSIC_GAME_TO_GET_TO_THE_OTHER_SIDE_LOOP
    )))

    /**
     * Represents the game 'Sky Battle'.
     */
    val SKY_BATTLE = register("sky_battle", GameType(::SkyBattleInstance, GameTypeSettings(
        "SKY BATTLE", CompanionSounds.MUSIC_GAME_SKY_BATTLE_LOOP
    )))

    /**
     * Represents the game 'Battle Box'.
     */
    val BATTLE_BOX = register("battle_box", GameType(::BattleBoxInstance, GameTypeSettings(
        "BATTLE BOX", CompanionSounds.MUSIC_GAME_BATTLE_BOX_LOOP
    )))

    /**
     * Represents the game 'Parkour Warrior Dojo'.
     */
    val PARKOUR_WARRIOR_DOJO = register("parkour_warrior_dojo", GameType(::ParkourWarriorDojoInstance, GameTypeSettings(
        "PARKOUR WARRIOR", CompanionSounds.MUSIC_GAME_PARKOUR_WARRIOR_LOOP
    )))
}
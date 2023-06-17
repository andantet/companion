package dev.andante.companion.api.sound

import dev.andante.companion.Companion
import net.minecraft.util.Identifier

object CompanionSounds {
    val MUSIC_GAME_HOLE_IN_THE_WALL_LOOP = create("music.game.hole_in_the_wall_loop")
    val MUSIC_GAME_TO_GET_TO_THE_OTHER_SIDE_LOOP = create("music.game.to_get_to_the_other_side_loop")
    val MUSIC_GAME_SKY_BATTLE_LOOP = create("music.game.sky_battle_loop")
    val MUSIC_GAME_BATTLE_BOX_LOOP = create("music.game.battle_box_loop")

    val MUSIC_GAME_PARKOUR_WARRIOR_LOOP = create("music.game.parkour_warrior_loop")
    val MUSIC_GAME_PARKOUR_WARRIOR_LOOP_FADE_OUT = create("music.game.parkour_warrior_loop_fade_out")

    val MUSIC_OVERTIME_INTRO = createMcc("music.global.overtime_intro_music")
    val MUSIC_OVERTIME_LOOP = createMcc("music.global.overtime_loop_music")
    val MUSIC_GAMEINTRO = createMcc("music.global.gameintro")
    val MUSIC_ROUNDENDMUSIC = createMcc("music.global.roundendmusic")

    private fun createMcc(id: String): Identifier {
        return Identifier("mcc", id)
    }

    private fun create(id: String): Identifier {
        return Identifier(Companion.MOD_ID, id)
    }
}

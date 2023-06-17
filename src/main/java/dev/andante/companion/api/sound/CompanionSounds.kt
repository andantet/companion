package dev.andante.companion.api.sound

import dev.andante.companion.Companion
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Identifier

object CompanionSounds {
    val MUSIC_GAME_PARKOUR_WARRIOR_LOOP = create("music.game.parkour_warrior_loop")
    val MUSIC_GAME_PARKOUR_WARRIOR_LOOP_FADE_OUT = create("music.game.parkour_warrior_loop_fade_out")

    val MUSIC_OVERTIME_INTRO = createMcc("music.global.overtime_intro_music")
    val MUSIC_OVERTIME_LOOP = createMcc("music.global.overtime_loop_music")

    private fun createMcc(id: String): Identifier {
        return Identifier("mcc", id)
    }

    private fun create(id: String): Identifier {
        return Identifier(Companion.MOD_ID, id)
    }
}

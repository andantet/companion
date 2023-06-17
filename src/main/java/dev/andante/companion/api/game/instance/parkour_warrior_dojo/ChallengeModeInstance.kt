package dev.andante.companion.api.game.instance.parkour_warrior_dojo

import dev.andante.companion.api.sound.CompanionSoundManager
import dev.andante.companion.api.sound.CompanionSounds

/**
 * An instance of Parkour Warrior Dojo challenge mode.
 */
class ChallengeModeInstance : ParkourWarriorDojoModeInstance() {
    override fun onInitialize() {
        CompanionSoundManager.stop(CompanionSounds.MUSIC_GAME_PARKOUR_WARRIOR_LOOP_FADE_OUT)
        CompanionSoundManager.playMusic(CompanionSounds.MUSIC_GAME_PARKOUR_WARRIOR_LOOP)
    }

    override fun onRemove() {
        CompanionSoundManager.playMusic(null)
        CompanionSoundManager.play(CompanionSounds.MUSIC_GAME_PARKOUR_WARRIOR_LOOP_FADE_OUT)
    }
}

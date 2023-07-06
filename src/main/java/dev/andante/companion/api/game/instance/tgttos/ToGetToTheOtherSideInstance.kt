package dev.andante.companion.api.game.instance.tgttos

import dev.andante.companion.api.game.instance.RoundBasedGameInstance
import dev.andante.companion.api.game.round.Round
import dev.andante.companion.api.game.type.GameType
import dev.andante.companion.api.setting.MusicSettings
import dev.andante.companion.api.sound.CompanionSoundManager
import dev.andante.companion.api.sound.CompanionSounds
import java.util.UUID

class ToGetToTheOtherSideInstance(type: GameType<ToGetToTheOtherSideInstance>, uuid: UUID)
    : RoundBasedGameInstance<ToGetToTheOtherSideRound, ToGetToTheOtherSideInstance>(type, uuid, ::ToGetToTheOtherSideRound) {
    override fun onRoundStart(round: Round, firstRound: Boolean) {
        // check to play custom music
        if (round is ToGetToTheOtherSideRound) {
            val data = round.data
            if (settings.musicSettingSupplier()) {
                if (data.map == ToGetToTheOtherSideRound.GameMap.TO_THE_DOME && MusicSettings.INSTANCE.toGetToTheOtherSideMapToTheDomeMusic) {
                    CompanionSoundManager.playMusic(CompanionSounds.MUSIC_GAME_TO_GET_TO_THE_OTHER_SIDE_LOOP_MAP_TO_THE_DOME)
                } else if (data.modifier == ToGetToTheOtherSideRound.Modifier.DOUBLE_TIME && MusicSettings.INSTANCE.toGetToTheOtherSideModifierDoubleTimeMusic) {
                    CompanionSoundManager.playMusic(CompanionSounds.MUSIC_GAME_TO_GET_TO_THE_OTHER_SIDE_LOOP_MODIFIER_DOUBLE_TIME)
                } else {
                    super.onRoundStart(round, firstRound)
                }
            }
        } else {
            super.onRoundStart(round, firstRound)
        }
    }
}

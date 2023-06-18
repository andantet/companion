package dev.andante.companion.api.game.instance.battle_box

import dev.andante.companion.api.game.instance.RoundBasedGameInstance
import dev.andante.companion.api.game.round.Round
import dev.andante.companion.api.game.type.GameType
import dev.andante.companion.api.sound.CompanionSoundManager
import java.util.UUID

class BattleBoxInstance(type: GameType<BattleBoxInstance>, uuid: UUID)
    : RoundBasedGameInstance<Round, BattleBoxInstance>(type, uuid, ::Round) {
    override fun onRoundInitialize(round: Round, isFirstRound: Boolean) {
        CompanionSoundManager.playMusic(type.settings.musicLoopSoundEvent)
    }

    override fun onRoundStart(round: Round, firstRound: Boolean) {
    }
}

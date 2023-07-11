package dev.andante.companion.game.instance.battle_box

import dev.andante.companion.game.instance.RoundBasedGameInstance
import dev.andante.companion.game.round.Round
import dev.andante.companion.game.type.GameType
import dev.andante.companion.sound.CompanionSoundManager
import java.util.UUID

class BattleBoxInstance(type: GameType<BattleBoxInstance>, uuid: UUID)
    : RoundBasedGameInstance<Round, BattleBoxInstance>(type, uuid, ::Round) {
    override fun onRoundInitialize(round: Round, isFirstRound: Boolean) {
        if (settings.musicSettingSupplier()) {
            CompanionSoundManager.playMusic(settings.musicLoopSoundEvent)
        }
    }

    override fun onRoundStart(round: Round, firstRound: Boolean) {
    }
}

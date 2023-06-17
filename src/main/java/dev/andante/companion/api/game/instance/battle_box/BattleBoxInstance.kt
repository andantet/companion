package dev.andante.companion.api.game.instance.battle_box

import dev.andante.companion.api.game.instance.RoundBasedGameInstance
import dev.andante.companion.api.game.round.Round
import dev.andante.companion.api.game.round.RoundManager
import dev.andante.companion.api.game.type.GameType
import dev.andante.companion.api.sound.CompanionSoundManager

class BattleBoxInstance(type: GameType<BattleBoxInstance>) : RoundBasedGameInstance<BattleBoxInstance>(type) {
    override val roundManager: RoundManager<BattleBoxInstance, Round<BattleBoxInstance>> = RoundManager(this) { object : Round<BattleBoxInstance>() {} }

    override fun onRoundInitialize(round: Round<BattleBoxInstance>, isFirstRound: Boolean) {
        CompanionSoundManager.playMusic(type.settings.musicLoopSoundEvent)
    }

    override fun onRoundStart(round: Round<BattleBoxInstance>, firstRound: Boolean) {
    }
}

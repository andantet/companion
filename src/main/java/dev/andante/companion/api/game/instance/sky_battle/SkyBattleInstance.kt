package dev.andante.companion.api.game.instance.sky_battle

import dev.andante.companion.api.game.instance.RoundBasedGameInstance
import dev.andante.companion.api.game.round.Round
import dev.andante.companion.api.game.round.RoundManager
import dev.andante.companion.api.game.type.GameType

class SkyBattleInstance(type: GameType<SkyBattleInstance>) : RoundBasedGameInstance<SkyBattleInstance>(type) {
    override val roundManager: RoundManager<SkyBattleInstance, Round<SkyBattleInstance>> = RoundManager(this) { object : Round<SkyBattleInstance>() {} }
}

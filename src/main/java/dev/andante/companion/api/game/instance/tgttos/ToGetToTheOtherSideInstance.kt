package dev.andante.companion.api.game.instance.tgttos

import dev.andante.companion.api.game.instance.RoundBasedGameInstance
import dev.andante.companion.api.game.round.Round
import dev.andante.companion.api.game.round.RoundManager
import dev.andante.companion.api.game.type.GameType

class ToGetToTheOtherSideInstance(type: GameType<ToGetToTheOtherSideInstance>) : RoundBasedGameInstance<ToGetToTheOtherSideInstance>(type) {
    override val roundManager: RoundManager<ToGetToTheOtherSideInstance, Round<ToGetToTheOtherSideInstance>> = RoundManager(this) { object : Round<ToGetToTheOtherSideInstance>() {} }
}

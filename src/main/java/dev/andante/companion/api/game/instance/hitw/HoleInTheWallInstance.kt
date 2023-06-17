package dev.andante.companion.api.game.instance.hitw

import dev.andante.companion.api.game.instance.RoundBasedGameInstance
import dev.andante.companion.api.game.round.Round
import dev.andante.companion.api.game.round.RoundManager
import dev.andante.companion.api.game.type.GameType

class HoleInTheWallInstance(type: GameType<HoleInTheWallInstance>) : RoundBasedGameInstance<HoleInTheWallInstance>(type) {
    override val roundManager: RoundManager<HoleInTheWallInstance, Round<HoleInTheWallInstance>> = RoundManager(this) { object : Round<HoleInTheWallInstance>() {} }
}

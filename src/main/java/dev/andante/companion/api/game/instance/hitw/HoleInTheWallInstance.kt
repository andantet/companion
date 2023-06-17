package dev.andante.companion.api.game.instance.hitw

import dev.andante.companion.api.game.instance.RoundBasedGameInstance
import dev.andante.companion.api.game.round.Round
import dev.andante.companion.api.game.type.GameType

class HoleInTheWallInstance(type: GameType<HoleInTheWallInstance>) : RoundBasedGameInstance<Round, HoleInTheWallInstance>(type, ::Round)

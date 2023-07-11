package dev.andante.companion.game.instance.hitw

import dev.andante.companion.game.instance.RoundBasedGameInstance
import dev.andante.companion.game.round.Round
import dev.andante.companion.game.type.GameType
import java.util.UUID

class HoleInTheWallInstance(type: GameType<HoleInTheWallInstance>, uuid: UUID)
    : RoundBasedGameInstance<Round, HoleInTheWallInstance>(type, uuid, ::Round)

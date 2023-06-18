package dev.andante.companion.api.game.instance.hitw

import dev.andante.companion.api.game.instance.RoundBasedGameInstance
import dev.andante.companion.api.game.round.Round
import dev.andante.companion.api.game.type.GameType
import java.util.UUID

class HoleInTheWallInstance(type: GameType<HoleInTheWallInstance>, uuid: UUID)
    : RoundBasedGameInstance<Round, HoleInTheWallInstance>(type, uuid, ::Round)

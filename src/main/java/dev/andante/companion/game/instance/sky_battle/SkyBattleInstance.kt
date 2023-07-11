package dev.andante.companion.game.instance.sky_battle

import dev.andante.companion.game.instance.RoundBasedGameInstance
import dev.andante.companion.game.round.Round
import dev.andante.companion.game.type.GameType
import java.util.UUID

class SkyBattleInstance(type: GameType<SkyBattleInstance>, uuid: UUID)
    : RoundBasedGameInstance<Round, SkyBattleInstance>(type, uuid, ::Round)

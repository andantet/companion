package dev.andante.companion.api.game.instance.sky_battle

import dev.andante.companion.api.game.instance.RoundBasedGameInstance
import dev.andante.companion.api.game.round.Round
import dev.andante.companion.api.game.type.GameType
import java.util.UUID

class SkyBattleInstance(type: GameType<SkyBattleInstance>, uuid: UUID)
    : RoundBasedGameInstance<Round, SkyBattleInstance>(type, uuid, ::Round)

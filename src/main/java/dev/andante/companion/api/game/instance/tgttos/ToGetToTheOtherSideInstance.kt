package dev.andante.companion.api.game.instance.tgttos

import dev.andante.companion.api.game.instance.RoundBasedGameInstance
import dev.andante.companion.api.game.type.GameType
import java.util.UUID

class ToGetToTheOtherSideInstance(type: GameType<ToGetToTheOtherSideInstance>, uuid: UUID)
    : RoundBasedGameInstance<ToGetToTheOtherSideRound, ToGetToTheOtherSideInstance>(type, uuid, ::ToGetToTheOtherSideRound)

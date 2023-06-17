package dev.andante.companion.api.game.instance.tgttos

import dev.andante.companion.api.game.instance.RoundBasedGameInstance
import dev.andante.companion.api.game.type.GameType

class ToGetToTheOtherSideInstance(type: GameType<ToGetToTheOtherSideInstance>) : RoundBasedGameInstance<ToGetToTheOtherSideRound, ToGetToTheOtherSideInstance>(type, ::ToGetToTheOtherSideRound)

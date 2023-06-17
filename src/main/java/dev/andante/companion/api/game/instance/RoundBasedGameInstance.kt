package dev.andante.companion.api.game.instance

import dev.andante.companion.api.game.round.Round
import dev.andante.companion.api.game.round.RoundManager
import dev.andante.companion.api.game.type.GameType
import dev.andante.companion.api.sound.CompanionSoundManager
import net.minecraft.text.Text

/**
 * An instance of a game type.
 */
@Suppress("LeakingThis")
abstract class RoundBasedGameInstance<R : Round, T : RoundBasedGameInstance<R, T>>(
    /**
     * The type of this instance.
     */
    type: GameType<T>
) : GameInstance<T>(type) {
    /**
     * The round manager for this instance.
     */
    open val roundManager: RoundManager<out Round, T> = RoundManager(this, ::Round)

    override fun onTitle(text: Text) {
        roundManager.onTitle(text)
    }

    override fun onGameMessage(text: Text, overlay: Boolean) {
        roundManager.onGameMessage(text)
    }

    open fun onRoundInitialize(round: Round, isFirstRound: Boolean) {
    }

    open fun onGameStart(round: Round) {
    }

    open fun onRoundStart(round: Round, firstRound: Boolean) {
        CompanionSoundManager.playMusic(type.settings.musicLoopSoundEvent)
    }

    open fun onRoundFinish(round: Round) {
        CompanionSoundManager.stopMusic()
    }

    open fun onGameEnd(round: Round) {
    }

    override fun renderDebugHud(textRendererConsumer: (Text) -> Unit) {
        roundManager.renderDebugHud(textRendererConsumer)
        textRendererConsumer(Text.literal(roundManager.toJson().toString()))
    }
}

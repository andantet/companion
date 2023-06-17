package dev.andante.companion.api.game.instance

import dev.andante.companion.api.game.round.Round
import dev.andante.companion.api.game.round.RoundManager
import dev.andante.companion.api.game.type.GameType
import dev.andante.companion.api.sound.CompanionSoundManager
import net.minecraft.text.Text

/**
 * An instance of a game type.
 */
abstract class RoundBasedGameInstance<T : RoundBasedGameInstance<T>>(
    /**
     * The type of this instance.
     */
    type: GameType<T>
) : GameInstance<T>(type) {
    abstract val roundManager: RoundManager<T, Round<T>>

    override fun onGameMessage(text: Text, overlay: Boolean) {
        roundManager.onGameMessage(text)
    }

    open fun onRoundInitialize(round: Round<T>, isFirstRound: Boolean) {
    }

    open fun onGameStart(round: Round<T>) {
    }

    open fun onRoundStart(round: Round<T>, firstRound: Boolean) {
        CompanionSoundManager.playMusic(type.settings.musicLoopSoundEvent)
    }

    open fun onRoundFinish(round: Round<T>) {
        CompanionSoundManager.stopMusic()
    }

    open fun onGameEnd(round: Round<T>) {
    }

    override fun renderDebugHud(textRendererConsumer: (Text) -> Unit) {
        roundManager.renderDebugHud(textRendererConsumer)
        textRendererConsumer(Text.literal(roundManager.toJson().toString()))
    }
}

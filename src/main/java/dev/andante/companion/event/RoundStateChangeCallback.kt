package dev.andante.companion.event

import dev.andante.companion.game.instance.RoundBasedGameInstance
import dev.andante.companion.game.round.Round
import dev.andante.companion.game.round.RoundManager
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory

fun interface RoundStateChangeCallback {
    fun onRoundState(state: RoundManager.State, oldState: RoundManager.State, gameInstance: RoundBasedGameInstance<*, *>, round: Round, currentRound: Int)

    companion object {
        /**
         * An event for the state of the current game instance changes.
         */
        val EVENT: Event<RoundStateChangeCallback> = EventFactory.createArrayBacked(RoundStateChangeCallback::class.java) { listeners -> RoundStateChangeCallback { state, oldState, gameInstance, round, currentRound ->
            listeners.forEach { callback -> callback.onRoundState(state, oldState, gameInstance, round, currentRound) }
        } }
    }
}

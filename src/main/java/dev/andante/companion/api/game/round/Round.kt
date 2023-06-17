package dev.andante.companion.api.game.round

import com.google.gson.JsonObject
import net.minecraft.text.Text

/**
 * Represents a round held in a [RoundManager].
 */
open class Round {
    open fun onGameMessage(text: Text) {
    }

    open fun renderDebugHud(textRendererConsumer: (Text) -> Unit) {
    }

    /**
     * Writes this game instance to JSON.
     */
    open fun toJson(json: JsonObject, state: RoundManager.State, currentRound: Int) {}
}

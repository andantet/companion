package dev.andante.companion.api.game.round

import com.google.gson.JsonObject
import dev.andante.companion.api.game.instance.GameInstance

/**
 * Represents a round held in a [RoundManager].
 */
abstract class Round<T : GameInstance<T>> {
    /**
     * Writes this game instance to JSON.
     */
    open fun toJson(json: JsonObject) {}
}

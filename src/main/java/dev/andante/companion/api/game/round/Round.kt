package dev.andante.companion.api.game.round

import com.google.gson.JsonObject

/**
 * Represents a round held in a [RoundManager].
 */
open class Round {
    /**
     * Writes this game instance to JSON.
     */
    open fun toJson(json: JsonObject) {}
}

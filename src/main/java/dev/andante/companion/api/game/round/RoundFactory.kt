package dev.andante.companion.api.game.round

/**
 * A factory for creating a round instance.
 */
fun interface RoundFactory<R> {
    /**
     * Creates an instance of a round.
     */
    fun create(): R
}

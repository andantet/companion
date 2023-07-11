package dev.andante.companion.game.round

/**
 * A factory for creating a round instance.
 */
fun interface RoundFactory<R : Round> {
    /**
     * Creates an instance of a round.
     */
    fun create(roundNumber: Int): R
}

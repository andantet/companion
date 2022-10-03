package dev.andante.mccic.api.game;

public enum GameState {
    /**
     * No game detected, usually a lobby.
     */
    NONE,

    /**
     * The player is in a queue state.
     */
    QUEUE,

    /**
     * Waiting for a game to begin.
     */
    WAITING_FOR_GAME,

    /**
     * A game is being played.
     */
    ACTIVE,

    /**
     * When the player has finished the round.
     */
    POST_ROUND_SELF,

    /**
     * When a round finishes.
     */
    POST_ROUND,

    /**
     * When the game has finished entirely.
     */
    POST_GAME
}

package dev.andante.mccic.api.game;

public enum GameState {
    /**
     * No game detected, usually a lobby.
     */
    NONE,

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
    POST_ROUND(true),

    /**
     * When the game has finished entirely.
     */
    POST_GAME(true);

    private final boolean ends;

    GameState(boolean ends) {
        this.ends = ends;
    }

    GameState() {
        this(false);
    }

    public boolean ends() {
        return this.ends;
    }
}

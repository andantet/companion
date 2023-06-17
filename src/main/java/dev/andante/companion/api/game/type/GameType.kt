package dev.andante.companion.api.game.type

import dev.andante.companion.api.game.instance.GameInstance

/**
 * A type of game.
 */
class GameType<T : GameInstance<T>>(
    /**
     * The factory to create an instance of this game.
     */
    private val instanceFactory: GameInstance.Factory<T>,

    /**
     * The game's settings.
     */
    val settings: GameTypeSettings
) {
    /**
     * The ID of this game type.
     */
    val id: String by lazy { GameTypes[this]!! }

    /**
     * Creates an instance of this game.
     */
    fun createInstance(): T {
        return instanceFactory.create(this)
    }

    override fun toString(): String {
        return "GameType[$id]"
    }

    companion object {
        /**
         * A map of all game type scoreboard names to their game types.
         */
        private val SCOREBOARD_NAME_TO_GAME_TYPE by lazy { buildMap {
            GameTypes.forEach { type ->
                this[type.settings.scoreboardName] = type
            }
        } }

        /**
         * @return the game type of the given scoreboard name
         */
        fun ofScoreboardName(name: String): GameType<*>? {
            return SCOREBOARD_NAME_TO_GAME_TYPE[name]
        }
    }
}

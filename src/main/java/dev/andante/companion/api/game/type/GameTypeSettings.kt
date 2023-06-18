package dev.andante.companion.api.game.type

import net.minecraft.util.Identifier

/**
 * The settings of a game type.
 */
data class GameTypeSettings(
    /**
     * The game's name as displayed at the top of the scoreboard sidebar.
     */
    val scoreboardName: String,

    /**
     * The music to loop throughout the game.
     */
    val musicLoopSoundEvent: Identifier,

    /**
     * Whether or not to play this game's music.
     */
    val musicSettingSupplier: () -> Boolean
)

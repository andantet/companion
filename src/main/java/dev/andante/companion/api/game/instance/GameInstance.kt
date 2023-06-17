package dev.andante.companion.api.game.instance

import dev.andante.companion.api.game.type.GameType
import net.minecraft.text.Text

/**
 * An instance of a game type.
 */
open class GameInstance<T : GameInstance<T>>(
    /**
     * The type of this instance.
     */
    val type: GameType<T>
) {
    /**
     * Called when the client receives a chat message.
     */
    open fun onGameMessage(text: Text, overlay: Boolean) {
    }

    /**
     * Called when a client receives a title packet.
     */
    open fun onTitle(text: Text) {
    }

    /**
     * Called when a client receives a subtitle packet.
     */
    open fun onSubtitle(text: Text) {
    }

    /*
     * Called when the game type is no longer detected as the type of this instance.
     */
    open fun onRemove() {
    }

    /**
     * Renders the debug hud.
     */
    open fun renderDebugHud(textRendererConsumer: (Text) -> Unit) {
    }

    fun interface Factory<T : GameInstance<T>> {
        fun create(type: GameType<T>): T
    }
}

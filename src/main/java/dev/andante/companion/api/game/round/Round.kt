package dev.andante.companion.api.game.round

import com.google.gson.JsonObject
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text

/**
 * Represents a round held in a [RoundManager].
 */
open class Round(
    /**
     * The round number of this round.
     */
    val roundNumber: Int
) {
    open fun tick(client: MinecraftClient) {
    }

    open fun onGameMessage(text: Text) {
    }

    open fun renderDebugHud(textRendererConsumer: (Text) -> Unit) {
    }

    /**
     * Writes this game instance to JSON.
     */
    open fun toJson(json: JsonObject) {}
}

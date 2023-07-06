package dev.andante.companion.api.game.round

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
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

    open fun <R : Round> getCodec(): Codec<in R> {
        return CODEC
    }

    companion object {
        /**
         * The codec of this class.
         */
        val CODEC: Codec<Round> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.INT.fieldOf("round_number")
                    .forGetter(Round::roundNumber)
            ).apply(instance, ::Round)
        }
    }
}

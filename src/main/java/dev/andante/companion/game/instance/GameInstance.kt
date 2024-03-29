package dev.andante.companion.game.instance

import com.google.gson.JsonElement
import dev.andante.companion.game.type.GameType
import dev.andante.companion.game.type.GameTypeSettings
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.MinecraftClient
import net.minecraft.client.sound.SoundInstance
import net.minecraft.text.Text
import java.util.UUID

/**
 * An instance of a game type.
 */
open class GameInstance<T : GameInstance<T>>(
    /**
     * The type of this instance.
     */
    val type: GameType<T>,

    /**
     * The uuid of this instance.
     */
    val uuid: UUID
) {
    /**
     * The settings of the game type.
     */
    val settings: GameTypeSettings get() = type.settings

    /**
     * Called every client tick.
     */
    open fun tick(client: MinecraftClient) {
    }

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

    /**
     * Called when a sound instance is played on the client.
     */
    open fun onPlaySound(soundInstance: SoundInstance) {
    }

    /*
     * Called when the game type is no longer detected as the type of this instance.
     */
    open fun onRemove() {
    }

    /**
     * Called after entities are rendered.
     */
    open fun afterRenderEntities(context: WorldRenderContext) {
    }

    /**
     * Renders the debug hud.
     */
    open fun renderDebugHud(textRendererConsumer: (Text) -> Unit) {
    }

    open fun toJson(): JsonElement? {
        return null
    }

    fun interface Factory<T : GameInstance<T>> {
        fun create(type: GameType<T>, uuid: UUID): T
    }
}

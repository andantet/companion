package dev.andante.companion.api.game.type

import dev.andante.companion.api.game.instance.GameInstance
import dev.andante.companion.api.helper.AssociationHelper
import net.minecraft.client.MinecraftClient
import java.util.UUID

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
        val world = MinecraftClient.getInstance().world
        val uuidString = world?.registryKey?.value?.path?.removePrefix("temp_world_")
        val uuid = uuidString?.let(UUID::fromString) ?: UUID.randomUUID()
        return instanceFactory.create(this, uuid)
    }

    override fun toString(): String {
        return "GameType[$id]"
    }

    companion object {
        /**
         * @return the game type of the given scoreboard name
         */
        val scoreboardNameAssociation by lazy { AssociationHelper.createAssociationFunction(GameTypes.getEntries()) { it.settings.scoreboardName } }
    }
}

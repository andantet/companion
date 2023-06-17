package dev.andante.companion.api.player

import com.google.gson.JsonObject
import dev.andante.companion.api.event.WorldJoinCallback
import dev.andante.companion.api.server.ServerTracker
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import java.util.UUID

data class PlayerReference(
    /**
     * The uuid of the player.
     */
    val uuid: UUID? = null,

    /**
     * The username of the player.
     */
    val username: () -> String? = {
        if (uuid == null) {
            null
        } else {
            val world = MinecraftClient.getInstance().world
            val profile = world?.getPlayerByUuid(uuid)?.gameProfile
            profile?.name ?: PLAYER_USERNAME_UUID_CACHE[uuid]
        }
    }
) {
    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }

        if (other !is PlayerReference) {
            return false
        }

        return other.uuid == uuid
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }

    fun toJson(): JsonObject {
        val json = JsonObject()
        uuid?.let { json.addProperty("uuid", it.toString()) }
        username()?.let { json.addProperty("username", it) }
        return json
    }

    companion object {
        /**
         * A cache storing the current world's player uuids to their usernames.
         */
        private val PLAYER_USERNAME_UUID_CACHE = mutableMapOf<UUID, String>()

        init {
            // update player cache
            WorldJoinCallback.EVENT.register {
                PLAYER_USERNAME_UUID_CACHE.clear()
            }

            ClientTickEvents.END_WORLD_TICK.register { world ->
                if (ServerTracker.isConnectedToMccIsland) {
                    world.players.forEach { player -> PLAYER_USERNAME_UUID_CACHE[player.uuid] = player.gameProfile.name }
                } else {
                    PLAYER_USERNAME_UUID_CACHE.clear()
                }
            }
        }

        /**
         * Creates a player reference from a username.
         */
        fun fromUsername(username: String): PlayerReference {
            val cache = PLAYER_USERNAME_UUID_CACHE.map { it.value to it.key }.toMap()
            return PlayerReference(cache[username]) { username }
        }
    }
}

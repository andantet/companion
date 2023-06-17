package dev.andante.companion.api

import net.minecraft.client.MinecraftClient

/**
 * Tracks server-based information.
 */
object ServerTracker {
    /**
     * The client instance.
     */
    private val CLIENT = MinecraftClient.getInstance()

    /**
     * The root domain for MCC: Island.
     */
    const val MCC_ISLAND_DOMAIN = "mccisland.net"

    /**
     * The client's current server address.
     */
    val CURRENT_SERVER_ADDRESS: String? get() = CLIENT.currentServerEntry?.address

    /**
     * Whether the client is connected to an MCC: Island server.
     */
    val IS_CONNECTED_TO_MCC_ISLAND: Boolean get() = CURRENT_SERVER_ADDRESS?.endsWith(MCC_ISLAND_DOMAIN) == true
}

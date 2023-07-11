package dev.andante.companion.server

import net.minecraft.client.MinecraftClient

/**
 * Tracks server-based information.
 */
object ServerTracker {
    /**
     * The root domain for MCC: Island.
     */
    const val MCC_ISLAND_DOMAIN = "mccisland.net"

    /**
     * The client instance.
     */
    private val client = MinecraftClient.getInstance()

    /**
     * The client's current server address.
     */
    val currentServerAddress: String? get() = client.currentServerEntry?.address

    /**
     * Whether the client is connected to an MCC: Island server.
     */
    val isConnectedToMccIsland: Boolean get() = currentServerAddress?.endsWith(MCC_ISLAND_DOMAIN) == true
}

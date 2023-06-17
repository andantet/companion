package dev.andante.companion

import dev.andante.companion.api.ServerTracker
import dev.andante.companion.api.game.GameTracker
import dev.andante.companion.api.game.type.GameTypes
import dev.andante.companion.api.sound.CompanionSoundManager
import dev.andante.companion.api.sound.CompanionSounds
import net.fabricmc.api.ClientModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Companion : ClientModInitializer {
    const val MOD_ID = "companion"
    private const val MOD_NAME = "MCCI: Companion"

    val LOGGER: Logger = LoggerFactory.getLogger(MOD_NAME)

    override fun onInitializeClient() {
        LOGGER.info("Initializing $MOD_NAME")

        CompanionSounds
        CompanionSoundManager

        GameTypes

        ServerTracker
        GameTracker
    }
}

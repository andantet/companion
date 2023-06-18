package dev.andante.companion

import dev.andante.companion.api.game.GameTracker
import dev.andante.companion.api.game.type.GameTypes
import dev.andante.companion.api.player.PlayerReference
import dev.andante.companion.api.server.ServerTracker
import dev.andante.companion.api.sound.CompanionSoundManager
import dev.andante.companion.api.sound.CompanionSounds
import dev.andante.companion.command.SettingsCommand
import dev.andante.companion.setting.SettingsContainer
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Companion : ClientModInitializer {
    const val MOD_ID = "companion"
    private const val MOD_NAME = "MCCI: Companion"

    val LOGGER: Logger = LoggerFactory.getLogger(MOD_NAME)

    override fun onInitializeClient() {
        LOGGER.info("Initializing $MOD_NAME")

        // sounds
        CompanionSounds
        CompanionSoundManager

        // player reference cache events
        PlayerReference

        // games
        GameTypes

        // trackers
        ServerTracker
        GameTracker

        // settings
        SettingsContainer.ALL_CONTAINERS.forEach(SettingsContainer<*>::load)

        // commands
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            SettingsCommand.register(dispatcher)
        }
    }
}

package dev.andante.companion

import dev.andante.companion.api.game.GameTracker
import dev.andante.companion.api.game.type.GameTypes
import dev.andante.companion.api.icon.IconManager
import dev.andante.companion.api.item.CustomItemManager
import dev.andante.companion.api.player.PlayerReference
import dev.andante.companion.api.player.ghost.GhostPlayerManager
import dev.andante.companion.api.player.position.serializer.PositionRecorderManager
import dev.andante.companion.api.server.ServerTracker
import dev.andante.companion.api.sound.CompanionSoundManager
import dev.andante.companion.api.sound.CompanionSounds
import dev.andante.companion.command.FetchCommand
import dev.andante.companion.command.SettingsCommand
import dev.andante.companion.screen.ScreenManager
import dev.andante.companion.setting.SettingsContainer
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Companion : ClientModInitializer {
    const val MOD_ID = "companion"
    private const val MOD_NAME = "MCCI: Companion"

    val LOGGER: Logger = LoggerFactory.getLogger(MOD_NAME)

    @Suppress("DeferredResultUnused")
    override fun onInitializeClient() {
        LOGGER.info("Initializing $MOD_NAME")

        // sounds
        CompanionSounds
        CompanionSoundManager

        // screens
        ScreenManager

        // player reference cache events
        PlayerReference

        // positions
        GhostPlayerManager
        PositionRecorderManager

        // games
        GameTypes

        // trackers
        ServerTracker
        GameTracker

        // settings
        SettingsContainer.ALL_CONTAINERS.forEach(SettingsContainer<*>::load)

        // fetchers
        IconManager.fetch()
        CustomItemManager.fetch()

        // commands
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            SettingsCommand.register(dispatcher)
            FetchCommand.register(dispatcher)
        }
    }
}

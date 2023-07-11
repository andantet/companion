package dev.andante.companion

import dev.andante.companion.command.FetchCommand
import dev.andante.companion.command.ParkourWarriorDojoCommand
import dev.andante.companion.command.SettingsCommand
import dev.andante.companion.game.GameTracker
import dev.andante.companion.game.instance.parkour_warrior.mode.dojo.challenge.DojoChallengeRunManager
import dev.andante.companion.game.type.GameTypes
import dev.andante.companion.item.CustomItemManager
import dev.andante.companion.player.PlayerReference
import dev.andante.companion.player.ghost.GhostPlayerManager
import dev.andante.companion.player.position.serializer.PositionRecorderManager
import dev.andante.companion.regex.RegexManager
import dev.andante.companion.screen.ScreenManager
import dev.andante.companion.server.ServerTracker
import dev.andante.companion.setting.SettingsContainer
import dev.andante.companion.sound.CompanionSoundManager
import dev.andante.companion.sound.CompanionSounds
import dev.andante.companion.tracker.AutomaticsTracker
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Companion : ClientModInitializer {
    const val MOD_ID = "companion"
    private const val MOD_NAME = "MCCI: Companion"

    private val LOGGER: Logger = LoggerFactory.getLogger(MOD_NAME)

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
        DojoChallengeRunManager.reload()

        // games
        GameTypes

        // trackers
        ServerTracker
        GameTracker
        AutomaticsTracker

        // settings
        SettingsContainer.ALL_CONTAINERS.forEach(SettingsContainer<*>::load)

        // fetchers
        RegexManager.fetch()
        CustomItemManager.fetch()

        // commands
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            SettingsCommand.register(dispatcher)
            FetchCommand.register(dispatcher)
            ParkourWarriorDojoCommand.register(dispatcher)
        }
    }
}

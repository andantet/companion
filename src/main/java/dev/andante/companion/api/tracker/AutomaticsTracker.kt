package dev.andante.companion.api.tracker

import dev.andante.companion.api.event.RoundStateChangeCallback
import dev.andante.companion.api.game.instance.RoundBasedGameInstance
import dev.andante.companion.api.game.round.Round
import dev.andante.companion.api.game.round.RoundManager.State
import dev.andante.companion.api.setting.AutoSettings
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ChatScreen

// TODO autos for parkour warrior: survivor (or any game that does not use a round manager)

/**
 * Tracks automatic functionality.
 */
object AutomaticsTracker {
    private val dummyChatScreen: ChatScreen by lazy {
        val screen = ChatScreen("")
        screen.init(MinecraftClient.getInstance(), 0, 0)
        screen
    }

    init {
        // register event
        RoundStateChangeCallback.EVENT.register(::onRoundStateChange)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onRoundStateChange(
        state: State,
        oldState: State,
        gameInstance: RoundBasedGameInstance<*, *>,
        round: Round,
        currentRound: Int
    ) {
        AutoSettings.INSTANCE.allAutoMessageSettings.forEach { settings ->
            if (settings.enabled) {
                if (state == settings.roundStateToSend) {
                    dummyChatScreen.sendMessage(settings.message, false)
                }
            }
        }
    }
}

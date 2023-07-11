package dev.andante.companion.screen

import dev.andante.companion.event.ScreenClosedCallback
import dev.andante.companion.icon.IconKeys
import dev.andante.companion.icon.IconManager
import dev.andante.companion.setting.HudSettings
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.gui.screen.ingame.HandledScreen

/**
 * Manages screens.
 */
object ScreenManager {
    private const val WARDROBE_TEXT = "WARDROBE"

    init {
        // TODO when releasing, remove this line
        if (FabricLoader.getInstance().isDevelopmentEnvironment) {
            // register after initialize
            ScreenEvents.AFTER_INIT.register { client, screen, _, _ ->
                val title = screen.title.string
                val settings = HudSettings.INSTANCE

                // render player in wardrobe
                if (settings.renderPlayerInWardrobe) {
                    if (title.contains(WARDROBE_TEXT)) {
                        if (screen is HandledScreen<*>) {
                            WardrobeScreenRenderer.preparePlayerRender(screen)

                            ScreenEvents.beforeRender(screen).register { _, context, _, _, _ ->
                                WardrobeScreenRenderer.renderPlayer(client, screen, context)
                            }
                        }
                    }
                }

                // beta test warning
                if (settings.closeBetaTestWarning) {
                    IconManager[IconKeys.GUI_BETA_TEST_WARNING]?.let { betaTestIcon ->
                        if (title.contains(betaTestIcon)) {
                            client.send { client.player?.closeHandledScreen() }
                        }
                    }
                }
            }
        }

        // register on remove
        ScreenClosedCallback.EVENT.register { WardrobeScreenRenderer.onScreenRemoved() }
    }
}

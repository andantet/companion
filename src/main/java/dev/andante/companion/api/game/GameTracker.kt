package dev.andante.companion.api.game

import com.google.gson.GsonBuilder
import dev.andante.companion.api.event.SoundPlayCallback
import dev.andante.companion.api.event.TitleEvents
import dev.andante.companion.api.event.WorldJoinCallback
import dev.andante.companion.api.game.instance.GameInstance
import dev.andante.companion.api.game.type.GameType
import dev.andante.companion.api.helper.FileHelper
import dev.andante.companion.api.scoreboard.ScoreboardAccessor
import dev.andante.companion.api.server.ServerTracker
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.sound.SoundInstance
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Tracks the active game.
 */
object GameTracker {
    private val LOGGER: Logger = LoggerFactory.getLogger("[MCCI: Companion] Game Tracker")

    /**
     * The active game instance.
     */
    var gameInstance: GameInstance<*>? = null; private set

    /**
     * The current game type.
     */
    val gameType: GameType<*>? get() = gameInstance?.type

    init {
        // register tick event
        ClientTickEvents.END_CLIENT_TICK.register(::tick)

        // register world join event
        WorldJoinCallback.EVENT.register { onJoinWorld() }

        // register chat event
        ClientReceiveMessageEvents.GAME.register { text, overlay -> gameInstance?.onGameMessage(text, overlay) }

        // register title events
        TitleEvents.TITLE.register { text -> gameInstance?.onTitle(text) }
        TitleEvents.SUBTITLE.register { text -> gameInstance?.onSubtitle(text) }

        // register render events
        WorldRenderEvents.AFTER_ENTITIES.register { context -> gameInstance?.afterRenderEntities(context) }

        // register sound play event
        SoundPlayCallback.EVENT.register(::onPlaySound)
    }

    private fun onPlaySound(soundInstance: SoundInstance) {
        gameInstance?.onPlaySound(soundInstance)
    }

    private fun tick(client: MinecraftClient) {
        // do not tick if not on mcc island
        if (!ServerTracker.isConnectedToMccIsland) {
            return
        }

        try {
            // check scoreboard objective
            ScoreboardAccessor.getSidebarObjective()?.let { objective ->
                val objectiveName = objective.displayName

                // retrieve the text containing the world name
                val worldNameText = objectiveName.siblings.elementAtOrNull(2)
                if (worldNameText != null) {
                    // retrieve the raw world name string
                    val worldName = worldNameText.string

                    // parse to game type
                    val type = GameType.scoreboardNameAssociation(worldName)
                    if (type != null) {
                        if (gameInstance == null || type != gameType) {
                            clearGameInstance()

                            // create instance
                            LOGGER.info("Creating game instance of type ${type.id}")
                            gameInstance = type.createInstance()
                        }

                        gameInstance?.tick(client)
                    } else {
                        clearGameInstance()
                    }
                } else {
                    clearGameInstance()
                }
            }
        } catch (throwable: Throwable) {
            LOGGER.error("Something went wrong ticking the MCCI: Companion game tracker", throwable)
        }
    }

    private fun onJoinWorld() {
        clearGameInstance()
    }

    private fun clearGameInstance() {
        val instance = gameInstance
        if (instance != null) {
            LOGGER.info("Clearing active game instance")

            // call instance on remove
            instance.onRemove()

            // flush to json
            if (gameType?.settings?.metricsSettingSupplier?.invoke() == true) {
                val file = FileHelper.companionFile("game_instances/${instance.type.id}/${instance.uuid}.json")
                file.parentFile.mkdirs()
                instance.toJson()?.let { json ->
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    file.writeText(gson.toJson(json))
                }
            }

            // remove instance
            gameInstance = null
        }
    }
}

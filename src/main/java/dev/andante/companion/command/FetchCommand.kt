package dev.andante.companion.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import dev.andante.companion.Companion
import dev.andante.companion.api.icon.IconManager
import dev.andante.companion.api.item.CustomItemManager
import dev.andante.companion.api.regex.RegexManager
import dev.andante.companion.api.serialization.CachedFetchedJsonMap
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.Text
import net.minecraft.util.Formatting

object FetchCommand {
    private const val FETCHING_ICONS_KEY = "command.${Companion.MOD_ID}.fetch.fetching"
    private const val FETCHED_ICONS_KEY = "command.${Companion.MOD_ID}.fetch.fetched"
    private val SOMETHING_WENT_WRONG_TEXT = Text.translatable("command.${Companion.MOD_ID}.something_went_wrong").formatted(Formatting.RED)

    fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        dispatcher.register(
            literal("${Companion.MOD_ID}:fetch")
                .then(
                    literal("icons")
                        .executes { execute(it, IconManager) }
                )
                .then(
                    literal("custom_items")
                        .executes { execute(it, CustomItemManager) }
                )
                .then(
                    literal("regex")
                        .executes { execute(it, RegexManager) }
                )
        )
    }

    private fun execute(context: CommandContext<FabricClientCommandSource>, manager: CachedFetchedJsonMap<*>): Int {
        context.source.sendFeedback(Text.translatable(FETCHING_ICONS_KEY, manager.cacheId))
        manager.fetch().invokeOnCompletion { throwable ->
            if (throwable != null) {
                context.source.sendFeedback(SOMETHING_WENT_WRONG_TEXT)
            } else {
                context.source.sendFeedback(Text.translatable(FETCHED_ICONS_KEY, manager.cacheId))
                context.source.sendFeedback(Text.literal(manager.asString()))
            }
        }
        return 1
    }
}

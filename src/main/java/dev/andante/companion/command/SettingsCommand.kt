package dev.andante.companion.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import dev.andante.companion.Companion
import dev.andante.companion.setting.SettingsContainer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.Text

object SettingsCommand {
    private val RELOADED_SETTINGS_TEXT = Text.translatable("command.${Companion.MOD_ID}.settings.reloaded_all_settings")
    private val SETTINGS_RELOAD_FAILURE_EXCEPTION = DynamicCommandExceptionType { id -> Text.translatable("command.${Companion.MOD_ID}.settings.settings_reload_failure", id) }

    fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        dispatcher.register(
            ClientCommandManager.literal("${Companion.MOD_ID}:settings")
                .then(
                    ClientCommandManager.literal("reload")
                        .executes(::executeReload)
                )
        )
    }

    @Throws(CommandSyntaxException::class)
    private fun executeReload(context: CommandContext<FabricClientCommandSource>): Int {
        SettingsContainer.ALL_CONTAINERS.forEach { container ->
            try {
                container.load()
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
                throw SETTINGS_RELOAD_FAILURE_EXCEPTION.create(container.id)
            }
        }

        context.source.sendFeedback(RELOADED_SETTINGS_TEXT)
        return 1
    }
}

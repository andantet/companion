package dev.andante.mccic.config.client.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.andante.mccic.api.MCCIC;
import dev.andante.mccic.config.ConfigHelper;
import dev.andante.mccic.config.ConfigHolder;
import dev.andante.mccic.config.client.ClientConfigRegistry;
import dev.andante.mccic.config.client.screen.MCCICConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

@Environment(EnvType.CLIENT)
public interface MCCICConfigCommand {
    static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
            literal(MCCIC.MOD_ID)
                .then(
                    literal("config")
                        .executes(MCCICConfigCommand::execute)
                        .then(
                            literal("reload")
                                .executes(MCCICConfigCommand::executeReload)
                        )
                )
        );
    }

    static int execute(CommandContext<FabricClientCommandSource> context) {
        MinecraftClient client = context.getSource().getClient();
        client.send(() -> client.setScreen(new MCCICConfigScreen(client.currentScreen)));
        return 1;
    }

    static int executeReload(CommandContext<FabricClientCommandSource> context) {
        ClientConfigRegistry.INSTANCE.forEach(ConfigHolder::load);
        context.getSource().sendFeedback(Text.literal("[%s] ".formatted(MCCIC.MOD_NAME)).append(ConfigHelper.RELOAD_DESCRIPTION_TEXT).formatted(Formatting.LIGHT_PURPLE));
        return 1;
    }
}

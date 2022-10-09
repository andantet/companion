package dev.andante.mccic.config.client.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.andante.mccic.api.MCCIC;
import dev.andante.mccic.config.ConfigHelper;
import dev.andante.mccic.config.ConfigHolder;
import dev.andante.mccic.config.client.ClientConfigRegistry;
import dev.andante.mccic.config.client.screen.MCCICConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.Consumer;
import java.util.function.Function;

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
                            literal("open")
                                .executes(MCCICConfigCommand::execute)
                        )
                        .then(
                            literal("reload")
                                .executes(MCCICConfigCommand::executeReload)
                        )
                )
        );
    }

    static void registerNewConfig(String id, Function<Screen, Screen> factory) {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            MCCICConfigCommand.registerNewConfig(dispatcher, arg -> {
                arg.then(literal(id).executes(context -> MCCICConfigCommand.openConfigScreen(context, factory)));
            });
        });
    }

    static void registerNewConfig(CommandDispatcher<FabricClientCommandSource> dispatcher, Consumer<LiteralArgumentBuilder<FabricClientCommandSource>> action) {
        LiteralArgumentBuilder<FabricClientCommandSource> arg = literal("open");
        action.accept(arg);
        dispatcher.register(literal(MCCIC.MOD_ID).then(literal("config").then(arg)));
    }

    static int execute(CommandContext<FabricClientCommandSource> context) {
        return openConfigScreen(context, MCCICConfigScreen::new);
    }

    static int executeReload(CommandContext<FabricClientCommandSource> context) {
        ClientConfigRegistry.INSTANCE.forEach(ConfigHolder::load);
        context.getSource().sendFeedback(Text.literal("[%s] ".formatted(MCCIC.MOD_NAME)).append(ConfigHelper.RELOAD_DESCRIPTION_TEXT).formatted(Formatting.LIGHT_PURPLE));
        return 1;
    }

    static int openConfigScreen(CommandContext<FabricClientCommandSource> context, Function<Screen, Screen> factory) {
        MinecraftClient client = context.getSource().getClient();
        client.send(() -> client.setScreen(factory.apply(client.currentScreen)));
        return 1;
    }
}

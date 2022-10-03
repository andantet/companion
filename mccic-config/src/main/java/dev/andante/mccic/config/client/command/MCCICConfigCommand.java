package dev.andante.mccic.config.client.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.andante.mccic.api.MCCIC;
import dev.andante.mccic.config.ConfigHolder;
import dev.andante.mccic.config.client.ClientConfigRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

@Environment(EnvType.CLIENT)
public interface MCCICConfigCommand {
    Text CONFIG_RELOADED = Text.translatable("text.%s.configReloaded".formatted(MCCIC.MOD_ID));

    static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
            literal(MCCIC.MOD_ID)
                .then(
                    literal("config")
                        .then(
                            literal("reload")
                                .executes(MCCICConfigCommand::reload)
                        )
                )
        );
    }

    static int reload(CommandContext<FabricClientCommandSource> context) {
        ClientConfigRegistry.INSTANCE.forEach(ConfigHolder::load);
        context.getSource().sendFeedback(CONFIG_RELOADED);
        return 1;
    }
}

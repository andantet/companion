package dev.andante.mccic.music.client;

import com.google.common.reflect.Reflection;
import com.mojang.brigadier.CommandDispatcher;
import dev.andante.mccic.api.client.UnicodeIconsStore;
import dev.andante.mccic.api.client.UnicodeIconsStore.Icon;
import dev.andante.mccic.api.client.event.MCCIChatEvent;
import dev.andante.mccic.api.client.tracker.GameTracker;
import dev.andante.mccic.api.event.EventResult;
import dev.andante.mccic.api.game.GameState;
import dev.andante.mccic.api.game.Games;
import dev.andante.mccic.config.client.ClientConfigRegistry;
import dev.andante.mccic.config.client.command.MCCICConfigCommand;
import dev.andante.mccic.music.MCCICMusic;
import dev.andante.mccic.music.client.config.HITWSoundOnOtherDeath;
import dev.andante.mccic.music.client.config.MusicClientConfig;
import dev.andante.mccic.music.client.config.MusicConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.player.PlayerEntity;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

@Environment(EnvType.CLIENT)
public final class MCCICMusicClientImpl implements MCCICMusic, ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientConfigRegistry.INSTANCE.registerAndLoad(MusicClientConfig.CONFIG_HOLDER, MusicConfigScreen::new);
        MCCICConfigCommand.registerNewConfig(ID, MusicConfigScreen::new);

        MCCIChatEvent.EVENT.register(this::onChatMessage);
        ClientCommandRegistrationCallback.EVENT.register(this::registerCommands);
        Reflection.initialize(GameSoundManager.class);
    }

    private void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(
                literal(MOD_ID + ":play_current")
                        .executes(context -> {
                            GameSoundManager.INSTANCE.playCurrent(MusicClientConfig::gameMusicVolume);
                            return 1;
                        })
        );

        dispatcher.register(
                literal(MOD_ID + ":try_fade_out")
                        .executes(context -> {
                            GameSoundManager.INSTANCE.tryFadeOut(MusicClientConfig.getConfig().overtimeTransitionTicks());
                            return 1;
                        })
        );
    }

    private EventResult onChatMessage(MCCIChatEvent.Context context) {
        GameTracker gameTracker = GameTracker.INSTANCE;
        if (gameTracker.getGame().orElse(null) == Games.HOLE_IN_THE_WALL) {
            MusicClientConfig config = MusicClientConfig.getConfig();
            HITWSoundOnOtherDeath deathSoundConfig = config.hitwSoundOnOtherDeath();
            boolean isActive = gameTracker.getGameState() == GameState.ACTIVE;
            if (!deathSoundConfig.isScore() || isActive) {
                MinecraftClient client = MinecraftClient.getInstance();
                PlayerEntity player = client.player;
                if (UnicodeIconsStore.isPrefixedWith(Icon.DEATH, context.message()) && !context.getRaw().contains(player.getEntityName())) {
                    GameSoundManager.INSTANCE.playHoleInTheWallOtherDeathSound(config, deathSoundConfig, player.getRandom());
                }
            }
        }

        return EventResult.pass();
    }
}

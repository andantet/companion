package dev.andante.mccic.music.client;

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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public final class MCCICMusicClientImpl implements MCCICMusic, ClientModInitializer {
    public static final GameSoundManager GAME_SOUND_MANAGER = new GameSoundManager(GameTracker.INSTANCE);

    @Override
    public void onInitializeClient() {
        ClientConfigRegistry.INSTANCE.registerAndLoad(MusicClientConfig.CONFIG_HOLDER, MusicConfigScreen::new);
        MCCICConfigCommand.registerNewConfig(ID, MusicConfigScreen::new);
        MCCIChatEvent.EVENT.register(this::onChatMessage);
    }

    public EventResult onChatMessage(MCCIChatEvent.Context context) {
        GameTracker gameTracker = GameTracker.INSTANCE;
        if (gameTracker.getGame().orElse(null) == Games.HOLE_IN_THE_WALL) {
            MusicClientConfig config = MusicClientConfig.getConfig();
            HITWSoundOnOtherDeath deathSoundConfig = config.hitwSoundOnOtherDeath();
            boolean isActive = gameTracker.getGameState() == GameState.ACTIVE;
            if (!deathSoundConfig.isScore() || isActive) {
                MinecraftClient client = MinecraftClient.getInstance();
                PlayerEntity player = client.player;
                if (UnicodeIconsStore.isPrefixedWith(Icon.DEATH, context.message()) && !context.getRaw().contains(player.getEntityName())) {
                    playHoleInTheWallOtherDeathSound(config, deathSoundConfig, client, player.getRandom(), !isActive);
                }
            }
        }

        return EventResult.pass();
    }

    public static void playHoleInTheWallOtherDeathSound(MusicClientConfig config, HITWSoundOnOtherDeath deathSoundConfig, MinecraftClient client, Random random, boolean deathVolume) {
        float volume = deathVolume ? config.gameMusicVolume() : config.gameMusicVolumeAfterDeath();
        float pitch = (deathSoundConfig.hasRandomPitch() ? random.nextFloat() * 0.17F : 0.0F) + 1.0F;
        for (Identifier sound : deathSoundConfig.getSounds()) {
            client.getSoundManager().play(new PositionedSoundInstance(sound, SoundCategory.MASTER, volume, pitch, random, false, 0, SoundInstance.AttenuationType.NONE, 0.0, 0.0, 0.0, true));
        }
    }
}

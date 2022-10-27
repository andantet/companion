package dev.andante.mccic.music.client;

import dev.andante.mccic.api.client.UnicodeIconsStore;
import dev.andante.mccic.api.client.UnicodeIconsStore.Icon;
import dev.andante.mccic.api.client.event.MCCIChatEvent;
import dev.andante.mccic.api.client.game.GameTracker;
import dev.andante.mccic.api.event.EventResult;
import dev.andante.mccic.api.game.Game;
import dev.andante.mccic.api.game.GameState;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

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
        MusicClientConfig config = MusicClientConfig.getConfig();
        HITWSoundOnOtherDeath deathSoundConfig = config.hitwSoundOnOtherDeath();
        Identifier[] sounds = deathSoundConfig.getSounds();
        if (sounds.length > 0) {
            GameTracker gameTracker = GameTracker.INSTANCE;
            if (gameTracker.getGame() == Game.HOLE_IN_THE_WALL) {
                boolean isActive = gameTracker.getGameState() == GameState.ACTIVE;
                if (!deathSoundConfig.isScore() || isActive) {
                    MinecraftClient client = MinecraftClient.getInstance();
                    PlayerEntity player = client.player;
                    if (UnicodeIconsStore.textContainsIcon(context.message(), Icon.DEATH) && !context.getRaw().contains(player.getEntityName())) {
                        float volume = isActive ? config.gameMusicVolumeAfterDeath() : config.gameMusicVolume();
                        float pitch = (deathSoundConfig.hasRandomPitch() ? player.getRandom().nextFloat() * 0.17F : 0.0F) + 1.0F;
                        for (Identifier sound : sounds) {
                            player.playSound(new SoundEvent(sound), volume, pitch);
                        }
                    }
                }
            }
        }

        return EventResult.pass();
    }
}

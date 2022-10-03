package dev.andante.mccic.music.client;

import dev.andante.mccic.api.client.event.MCCIClientDeathScreenEvent;
import dev.andante.mccic.api.client.event.MCCIClientRespawnEvent;
import dev.andante.mccic.api.client.event.MCCIGameEvents;
import dev.andante.mccic.api.client.game.GameTracker;
import dev.andante.mccic.api.game.Game;
import dev.andante.mccic.api.game.GameState;
import dev.andante.mccic.music.MCCICSounds;
import dev.andante.mccic.music.client.sound.MCCIGameMusicInstance;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvent;

@Environment(EnvType.CLIENT)
public class MusicTracker {
    private final GameTracker gameTracker;
    private final MinecraftClient client;

    private SoundInstance lastSound;

    public MusicTracker(GameTracker gameTracker) {
        this.gameTracker = gameTracker;
        this.client = MinecraftClient.getInstance();

        MCCIGameEvents.STATE_UPDATE.register(this::onStateUpdate);
        MCCIClientRespawnEvent.EVENT.register(this::onRespawn);
        MCCIClientDeathScreenEvent.EVENT.register(this::onDeathScreen);
    }

    protected void onStateUpdate(GameState state, GameState oldState) {
        switch (state) {
            case ACTIVE -> this.playCurrentGameMusic(MCCICMusicClientImpl.getConfig().musicVolume());
            case POST_ROUND_SELF, POST_ROUND, POST_GAME -> {
                SoundManager soundManager = this.client.getSoundManager();

                if (this.lastSound != null) {
                    soundManager.stop(this.lastSound);
                }

                if (state == GameState.POST_ROUND_SELF) {
                    soundManager.play(PositionedSoundInstance.master(new SoundEvent(MCCICSounds.EARLY_ELIMINATION), 1.0f, 1.0f), 7);
                }
            }
        }
    }

    protected void onDeathScreen(DeathScreen screen) {
        SoundManager soundManager = this.client.getSoundManager();
        soundManager.stop(this.lastSound);
    }

    protected void onRespawn(ClientPlayerEntity player) {
        this.playCurrentGameMusic(MCCICMusicClientImpl.getConfig().musicVolumeAfterDeath());
    }

    public void playCurrentGameMusic(float volume) {
        SoundManager soundManager = this.client.getSoundManager();
        soundManager.stop(this.lastSound);

        Game game = this.gameTracker.getGame();
        SoundInstance sound = this.lastSound = new MCCIGameMusicInstance(game, volume);
        soundManager.play(sound);
    }
}

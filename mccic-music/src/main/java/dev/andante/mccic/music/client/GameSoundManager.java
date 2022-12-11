package dev.andante.mccic.music.client;

import dev.andante.mccic.api.client.event.*;
import dev.andante.mccic.api.client.tracker.GameTracker;
import dev.andante.mccic.api.game.GameRegistry;
import dev.andante.mccic.api.game.GameState;
import dev.andante.mccic.api.game.Games;
import dev.andante.mccic.music.MCCICMusic;
import dev.andante.mccic.music.MCCICSounds;
import dev.andante.mccic.music.client.config.MusicClientConfig;
import dev.andante.mccic.music.client.sound.VolumeAdjustableSoundInstance;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class GameSoundManager {
    private final GameTracker gameTracker;
    private SoundManager soundManager;
    private SoundInstance lastSound;

    public static final Identifier OVERTIME_MUSIC_ID = new Identifier("mcc", "games.global.music.overtime_intro_music");

    public GameSoundManager(GameTracker gameTracker) {
        this.gameTracker = gameTracker;

        ClientTickEvents.START_CLIENT_TICK.register(this::tick);
        MCCISoundPlayEvent.EVENT.register(this::onSoundPlay);
        MCCIGameEvents.STATE_UPDATE.register(this::onStateUpdate);
        MCCIClientRespawnEvent.EVENT.register(this::onRespawn);
    }

    private void tick(MinecraftClient client) {
        this.soundManager = client.getSoundManager();
    }

    private void onSoundPlay(MCCISoundPlayEvent.Context context) {
        Identifier id = context.getSoundFileIdentifier();
        if (id.equals(OVERTIME_MUSIC_ID)) {
            this.stopLastSound();
        }
    }

    protected void onStateUpdate(GameState state, GameState oldState) {
        switch (state) {
            case ACTIVE -> this.playCurrent(MusicClientConfig::gameMusicVolume);
            case POST_ROUND_SELF -> {
                MusicClientConfig config = MusicClientConfig.getConfig();

                if (this.gameTracker.getGame().orElse(null) == Games.TGTTOS) {
                    if (config.stopMusicOnChickenHit()) {
                        this.stopLastSound();
                    }
                } else {
                    if (config.stopMusicOnDeath()) {
                        this.stopLastSound();
                    }
                }

                this.soundManager.play(PositionedSoundInstance.master(SoundEvent.of(MCCICSounds.EARLY_ELIMINATION), 1.0f, config.sfxVolume()), 7);
            }
            case POST_ROUND, POST_GAME -> {
                this.stopLastSound();
            }
        }
    }

    protected void onRespawn(ClientPlayerEntity player) {
        if (MusicClientConfig.getConfig().stopMusicOnDeath()) {
            if (!this.gameTracker.getGameState().ends()) {
                this.playCurrent(MusicClientConfig::gameMusicVolumeAfterDeath);
            }
        }
    }

    public void playCurrent(Function<MusicClientConfig, Float> volume) {
        this.gameTracker.getGame().ifPresent(game -> {
            Identifier id = new Identifier(MCCICMusic.MOD_ID, "game.%s".formatted(GameRegistry.INSTANCE.getId(game)));
            SoundInstance sound = new VolumeAdjustableSoundInstance(id, () -> volume.apply(MusicClientConfig.getConfig()));
            this.stopLastSound();
            this.lastSound = sound;
            this.soundManager.play(sound);
        });
    }

    public void stopLastSound() {
        if (this.lastSound != null) {
            this.soundManager.stop(this.lastSound);
        }
    }
}

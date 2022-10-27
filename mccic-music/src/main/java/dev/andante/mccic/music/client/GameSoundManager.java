package dev.andante.mccic.music.client;

import dev.andante.mccic.api.client.event.MCCIClientDeathScreenEvent;
import dev.andante.mccic.api.client.event.MCCIClientLoginHelloEvent;
import dev.andante.mccic.api.client.event.MCCIClientRespawnEvent;
import dev.andante.mccic.api.client.event.MCCIGameEvents;
import dev.andante.mccic.api.client.game.GameTracker;
import dev.andante.mccic.api.game.GameState;
import dev.andante.mccic.music.MCCICSounds;
import dev.andante.mccic.music.client.config.MusicClientConfig;
import dev.andante.mccic.music.client.sound.VolumeAdjustableSoundInstance;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.network.packet.s2c.login.LoginHelloS2CPacket;
import net.minecraft.sound.SoundEvent;

import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class GameSoundManager {
    private final GameTracker gameTracker;
    private SoundManager soundManager;
    private SoundInstance lastSound;

    public GameSoundManager(GameTracker gameTracker) {
        this.gameTracker = gameTracker;

        MCCIClientLoginHelloEvent.EVENT.register(this::onClientLoginHello);
        MCCIGameEvents.TIMER_UPDATE.register(this::onTimerUpdate);
        MCCIGameEvents.STATE_UPDATE.register(this::onStateUpdate);
        MCCIClientRespawnEvent.EVENT.register(this::onRespawn);
        MCCIClientDeathScreenEvent.EVENT.register(this::onDeathScreen);
    }

    protected void onClientLoginHello(ClientLoginNetworkHandler handler, LoginHelloS2CPacket packet) {
        this.soundManager = MinecraftClient.getInstance().getSoundManager();
    }

    protected void onTimerUpdate(int time, int lastTime) {
        if (this.gameTracker.getGameState() == GameState.ACTIVE) {
            if (time == 10) { // TODO replace with SoundEvent event (overtime check)
                this.soundManager.stop(this.lastSound);
            }
        }
    }

    protected void onStateUpdate(GameState state, GameState oldState) {
        switch (state) {
            case ACTIVE -> this.playCurrent(MusicClientConfig::gameMusicVolume);
            case POST_ROUND_SELF, POST_ROUND, POST_GAME -> {
                if (this.lastSound != null) {
                    this.soundManager.stop(this.lastSound);
                }

                if (state == GameState.POST_ROUND_SELF) {
                    this.soundManager.play(PositionedSoundInstance.master(new SoundEvent(MCCICSounds.EARLY_ELIMINATION), 1.0f, 1.0f), 7);
                }
            }
        }
    }

    protected void onDeathScreen(DeathScreen screen) {
        this.soundManager.stop(this.lastSound);
    }

    protected void onRespawn(ClientPlayerEntity player) {
        if (!this.gameTracker.getGameState().ends()) {
            this.playCurrent(MusicClientConfig::gameMusicVolumeAfterDeath);
        }
    }

    public void playCurrent(Function<MusicClientConfig, Float> volume) {
        SoundInstance sound = new VolumeAdjustableSoundInstance(this.gameTracker.getGameSoundId(), () -> volume.apply(MusicClientConfig.getConfig()));
        this.soundManager.stop(this.lastSound);
        this.lastSound = sound;
        this.soundManager.play(sound);
    }
}

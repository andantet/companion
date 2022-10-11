package dev.andante.mccic.music.client.sound;

import dev.andante.mccic.api.game.Game;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;

public class GameMusicInstance extends PositionedSoundInstance {
    public GameMusicInstance(Game game, float volume) {
        super(game.getSoundId(), SoundCategory.VOICE, volume, 1.0F, SoundInstance.createRandom(), true, 0, SoundInstance.AttenuationType.NONE, 0.0, 0.0, 0.0, true);
    }

    @Override
    public float getVolume() {
        return this.volume;
    }

    @Override
    public float getPitch() {
        return this.pitch;
    }
}

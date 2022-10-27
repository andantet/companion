package dev.andante.mccic.music.client.sound;

import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

/**
 * A sound instance with supplied volume and pitch that updates when the supplied value updates.
 */
public class VolumeAdjustableSoundInstance extends PositionedSoundInstance implements TickableSoundInstance {
    private final Supplier<Float> volume, pitch;

    public VolumeAdjustableSoundInstance(Identifier id, Supplier<Float> volume, Supplier<Float> pitch) {
        super(id, SoundCategory.MASTER, 1.0F, 1.0F, SoundInstance.createRandom(), true, 0, AttenuationType.NONE, 0.0D, 0.0D, 0.0D, true);

        this.volume = volume;
        this.pitch = pitch;
    }

    public VolumeAdjustableSoundInstance(Identifier id, Supplier<Float> volume) {
        this(id, volume, () -> 1.0F);
    }

    @Override
    public void tick() {
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public float getVolume() {
        return this.volume.get();
    }

    @Override
    public float getPitch() {
        return this.pitch.get();
    }
}

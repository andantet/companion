package dev.andante.mccic.api.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public interface SoundFactory {
    static PositionedSoundInstance create(Identifier id) {
        return create(id, SoundCategory.MASTER, 1.0F, 1.0F);
    }

    static PositionedSoundInstance create(Identifier id, SoundCategory category, float volume, float pitch) {
        return new PositionedSoundInstance(id, category, volume, pitch, SoundInstance.createRandom(), false, 0, SoundInstance.AttenuationType.NONE, 0.0D, 0.0D, 0.0D, true);
    }
}

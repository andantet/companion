package dev.andante.companion.data

import dev.andante.companion.api.sound.CompanionSounds
import dev.andante.companion.data.sound.SoundBuilder
import dev.andante.companion.data.sound.SoundProvider
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput

class CompanionSoundProvider(dataOutput: FabricDataOutput) : SoundProvider(dataOutput) {
    override fun generateSounds(soundGenerator: SoundGenerator) {
        soundGenerator.addDefault(CompanionSounds.MUSIC_GAME_PARKOUR_WARRIOR_LOOP, SoundBuilder::stream)
        soundGenerator.addDefault(CompanionSounds.MUSIC_GAME_PARKOUR_WARRIOR_LOOP_FADE_OUT)
    }
}

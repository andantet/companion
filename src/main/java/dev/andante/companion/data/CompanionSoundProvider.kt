package dev.andante.companion.data

import dev.andante.companion.api.sound.CompanionSounds
import dev.andante.companion.data.sound.SoundBuilder
import dev.andante.companion.data.sound.SoundProvider
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput

class CompanionSoundProvider(dataOutput: FabricDataOutput) : SoundProvider(dataOutput) {
    override fun generateSounds(soundGenerator: SoundGenerator) {
        soundGenerator.addDefault(CompanionSounds.MUSIC_GAME_HOLE_IN_THE_WALL_LOOP, SoundBuilder::stream)
        soundGenerator.addDefault(CompanionSounds.MUSIC_GAME_TO_GET_TO_THE_OTHER_SIDE_LOOP, SoundBuilder::stream)
        soundGenerator.addDefault(CompanionSounds.MUSIC_GAME_TO_GET_TO_THE_OTHER_SIDE_LOOP_MODIFIER_DOUBLE_TIME, SoundBuilder::stream)
        soundGenerator.addDefault(CompanionSounds.MUSIC_GAME_TO_GET_TO_THE_OTHER_SIDE_LOOP_MAP_TO_THE_DOME, SoundBuilder::stream)
        soundGenerator.addDefault(CompanionSounds.MUSIC_GAME_SKY_BATTLE_LOOP, SoundBuilder::stream)
        soundGenerator.addDefault(CompanionSounds.MUSIC_GAME_BATTLE_BOX_LOOP, SoundBuilder::stream)

        soundGenerator.addDefault(CompanionSounds.MUSIC_GAME_PARKOUR_WARRIOR_LOOP, SoundBuilder::stream)
        soundGenerator.addDefault(CompanionSounds.MUSIC_GAME_PARKOUR_WARRIOR_LOOP_FADE_OUT)
    }
}

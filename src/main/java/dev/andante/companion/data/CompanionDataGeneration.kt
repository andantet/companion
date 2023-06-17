package dev.andante.companion.data

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator

class CompanionDataGeneration : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(generator: FabricDataGenerator) {
        val pack = generator.createPack()
        pack.addProvider(::CompanionSoundProvider)
    }
}

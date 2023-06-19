package dev.andante.companion.setting

import com.mojang.serialization.Codec
import dev.andante.companion.api.helper.FileHelper.companionFile
import dev.andante.companion.api.serialization.SerializableContainer

/**
 * A container for settings.
 */
open class SettingsContainer<T>(id: String, codec: Codec<T>, default: T) : SerializableContainer<T>(
    id, codec, default,
    companionFile("settings/$id.json"),
    companionFile("settings/$id.json_old")
) {
    companion object {
        /**
         * All settings containers.
         */
        val ALL_CONTAINERS = setOf(
            MusicSettings.CONTAINER,
            MetricsSettings.CONTAINER
        )
    }
}

package dev.andante.companion.api.icon

import com.mojang.serialization.Codec
import dev.andante.companion.api.serialization.CachedFetchedJsonMap

/**
 * Manages MCC: Island unicode icons.
 */
object IconManager : CachedFetchedJsonMap<String>(
    "icons",
    "https://gist.githubusercontent.com/andantet/702a32539377b363bc6ae0c09d2982d2/raw/unicode_icons_v2.json",
    Codec.STRING
)

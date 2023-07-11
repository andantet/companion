package dev.andante.companion.item

import com.mojang.serialization.Codec
import dev.andante.companion.serialization.CachedFetchedJsonMap

/**
 * Manages MCC: Island custom item model data.
 */
object CustomItemManager : CachedFetchedJsonMap<Int>(
    "custom_items",
    "https://gist.githubusercontent.com/andantet/702a32539377b363bc6ae0c09d2982d2/raw/custom_items.json",
    Codec.INT
)

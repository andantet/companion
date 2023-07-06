package dev.andante.companion.api.extension

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import java.util.Optional

fun <A : Any> Codec<A>.nullableFieldOf(name: String): MapCodec<A?> {
    return optionalFieldOf(name).xmap(
        { it.orElse(null) },
        { Optional.ofNullable(it) }
    ).orElse(null)
}

fun <A : Any?> MapCodec<A>.functionally(): MapCodec<() -> A> {
    return xmap({ { it } }, { it() })
}

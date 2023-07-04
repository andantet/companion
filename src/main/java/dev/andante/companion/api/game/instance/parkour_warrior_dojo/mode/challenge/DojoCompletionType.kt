package dev.andante.companion.api.game.instance.parkour_warrior_dojo.mode.challenge

import com.mojang.serialization.Codec
import net.minecraft.util.StringIdentifiable

enum class DojoCompletionType(val id: String) : StringIdentifiable {
    STANDARD("standard"),
    ADVANCED("advanced"),
    EXPERT("expert"),
    INCOMPLETE("incomplete");

    override fun asString(): String {
        return id
    }

    companion object {
        /**
         * The codec of this class.
         */
        val CODEC: Codec<DojoCompletionType> = StringIdentifiable.createCodec(DojoCompletionType::values)
    }
}

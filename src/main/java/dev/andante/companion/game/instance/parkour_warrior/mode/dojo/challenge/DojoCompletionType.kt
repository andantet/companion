package dev.andante.companion.game.instance.parkour_warrior.mode.dojo.challenge

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

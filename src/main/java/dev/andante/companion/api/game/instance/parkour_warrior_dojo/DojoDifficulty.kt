package dev.andante.companion.api.game.instance.parkour_warrior_dojo

import com.mojang.serialization.Codec
import dev.andante.companion.api.helper.AssociationHelper
import net.minecraft.util.StringIdentifiable

/**
 * The difficulty of a section in Parkour Warrior: Dojo.
 */
enum class DojoDifficulty(val id: String, val endingMedals: Int) : StringIdentifiable {
    EASY("easy", 1),
    MEDIUM("medium", 2),
    HARD("hard", 3);

    override fun asString(): String {
        return id
    }

    companion object {
        /**
         * The codec of this class.
         */
        val CODEC: Codec<DojoDifficulty> = StringIdentifiable.createCodec(DojoDifficulty::values)

        /**
         * @return the difficulty of the given ending medals
         */
        val endingMedalsAssociation = AssociationHelper.createAssociationFunction(values(), DojoDifficulty::endingMedals)
    }
}

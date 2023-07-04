package dev.andante.companion.api.game.instance.parkour_warrior_dojo

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.andante.companion.api.helper.AssociationHelper
import net.minecraft.util.StringIdentifiable

/**
 * A store of section information.
 */
data class DojoSection(
    /**
     * The section's branch.
     */
    val branch: Branch,

    /**
     * The section's branch number.
     */
    val branchNumber: Int,

    /**
     * The individual section's number.
     */
    val sectionNumber: Int,

    /**
     * The individual section's name.
     */
    val sectionName: String
) {
    enum class Branch (
        val id: String,

        /**
         * The abbreviated version displayed on the section title.
         */
        val titleAbbreviation: String
    ) : StringIdentifiable {
        MAIN("main", "M"),
        BONUS("bonus", "B");

        override fun asString(): String {
            return id
        }

        companion object {
            /**
             * The codec of this class.
             */
            val CODEC: Codec<Branch> = StringIdentifiable.createCodec(Branch::values)

            /**
             * @return the mode of the given chat string
             */
            val titleAbbreviationAssocation = AssociationHelper.createAssociationFunction(values(), Branch::titleAbbreviation)
        }
    }

    companion object {
        /**
         * The codec of this class.
         */
        val CODEC: Codec<DojoSection> = RecordCodecBuilder.create { instance ->
            instance.group(
                Branch.CODEC.fieldOf("branch").forGetter(DojoSection::branch),
                Codec.INT.fieldOf("brach_number").forGetter(DojoSection::branchNumber),
                Codec.INT.fieldOf("section_number").forGetter(DojoSection::sectionNumber),
                Codec.STRING.fieldOf("section_name").forGetter(DojoSection::sectionName),
            ).apply(instance, ::DojoSection)
        }
    }
}

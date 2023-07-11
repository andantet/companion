package dev.andante.companion.game.instance.parkour_warrior

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.andante.companion.game.instance.parkour_warrior.mode.dojo.challenge.CompletedSection
import dev.andante.companion.helper.AssociationHelper
import net.minecraft.util.StringIdentifiable
import net.minecraft.util.Util

/**
 * A store of section information.
 */
data class ParkourWarriorSection(
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
    /**
     * @return this section as a [CompletedSection]
     */
    fun toCompleted(startedAt: Long): CompletedSection {
        return CompletedSection(Util.getMeasuringTimeMs() - startedAt, this)
    }

    enum class Branch (
        val id: String,

        /**
         * The abbreviated version displayed on the section title.
         */
        val titleAbbreviation: String
    ) : StringIdentifiable {
        MAIN("main", "M"),
        BONUS("bonus", "B"),
        SECTION("section", "S"),
        FINAL("final", "F");

        override fun asString(): String {
            return id
        }

        companion object {
            /**
             * The codec of this class.
             */
            val CODEC: Codec<Branch> = StringIdentifiable.createCodec(Branch::values)

            /**
             * @return the branch of the given chat string
             */
            val titleAbbreviationAssocation = AssociationHelper.createAssociationFunction(entries, Branch::titleAbbreviation)
        }
    }

    companion object {
        /**
         * The codec of this class.
         */
        val CODEC: Codec<ParkourWarriorSection> = RecordCodecBuilder.create { instance ->
            instance.group(
                Branch.CODEC.fieldOf("branch").forGetter(ParkourWarriorSection::branch),
                Codec.INT.fieldOf("branch_number").orElse(-1).forGetter(ParkourWarriorSection::branchNumber),
                Codec.INT.fieldOf("section_number").forGetter(ParkourWarriorSection::sectionNumber),
                Codec.STRING.fieldOf("section_name").forGetter(ParkourWarriorSection::sectionName),
            ).apply(instance, ::ParkourWarriorSection)
        }
    }
}

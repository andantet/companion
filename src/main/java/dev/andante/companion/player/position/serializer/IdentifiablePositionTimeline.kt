package dev.andante.companion.player.position.serializer

/**
 * A position timeline with an id.
 */
data class IdentifiablePositionTimeline(
    /**
     * The id of this timeline.
     */
    val id: String,

    /**
     * The timeline itself.
     */
    val timeline: PositionTimeline
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other !is IdentifiablePositionTimeline) {
            return false
        }

        return other.id == id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

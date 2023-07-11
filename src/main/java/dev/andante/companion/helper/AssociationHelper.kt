package dev.andante.companion.helper

object AssociationHelper {
    fun <T, R> createAssociationFunction(values: Array<T>, mapper: (T) -> R): (R) -> T? {
        val map = values.associateBy(mapper)
        return { map[it] }
    }

    fun <T, R> createAssociationFunction(values: Collection<T>, mapper: (T) -> R): (R) -> T? {
        val map = values.associateBy(mapper)
        return { map[it] }
    }
}

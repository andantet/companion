package dev.andante.companion.api.registry

import java.util.function.Consumer

/**
 * A basic string-object registry.
 */
open class BasicRegistry<T : Any> {
    private val entries = mutableListOf<T>()
    private val keys = mutableListOf<String>()
    private val keyToEntryMap = mutableMapOf<String, T>()
    private val entryToKeyMap = mutableMapOf<T, String>()

    /**
     * The amount of registered entries in this registry.
     */
    val size: Int get() = entries.size

    /**
     * Registers [entry] to the registry under [key].
     * @return the passed [entry]
     */
    fun register(key: String, entry: T): T {
        if (keys.contains(key)) {
            throw UnsupportedOperationException("Key $key already registered")
        }

        entries.add(entry)
        keys.add(key)

        keyToEntryMap[key] = entry
        entryToKeyMap[entry] = key

        return entry
    }

    /**
     * @return the value that is assigned [key], or `null` if it is not registered
     */
    operator fun get(key: String): T? {
        return keyToEntryMap[key]
    }

    /**
     * @return the key assigned to [entry], or `null` if it is not registered
     */
    operator fun get(entry: T): String? {
        return entryToKeyMap[entry]
    }

    /**
     * @return the index of the entry
     */
    fun getId(entry: T): Int {
        return entries.indexOf(entry)
    }

    /**
     * @return the entries of this registry
     */
    fun getEntries(): List<T> {
        return entries
    }

    /**
     * Performs [action] on every registered entry.
     */
    fun forEach(action: Consumer<T>) {
        entries.forEach(action)
    }
}

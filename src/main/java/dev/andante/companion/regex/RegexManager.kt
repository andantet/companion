package dev.andante.companion.regex

import com.mojang.serialization.Codec
import dev.andante.companion.serialization.CachedFetchedJsonMap

/**
 * Manages remotely-hosted regular expressions.
 */
object RegexManager : CachedFetchedJsonMap<Regex>(
    "regexes",
    "https://gist.githubusercontent.com/andantet/702a32539377b363bc6ae0c09d2982d2/raw/regexes.json",
    Codec.STRING.xmap(::Regex, Regex::pattern)
) {
    /**
     * @return whether the regular expression [key] matches with the string [string]
     * @param key the key of the regular expression
     * @param string the string to be matched
     */
    fun matches(key: String, string: String): Boolean {
        return this[key]?.matches(string) == true
    }
}

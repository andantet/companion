package dev.andante.companion.api.text

import org.intellij.lang.annotations.RegExp

object TextRegex {
    @RegExp
    const val USERNAME_PATTERN = "[a-zA-Z0-9_]{2,16}"
}

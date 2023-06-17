package dev.andante.companion.api.helper

import dev.andante.companion.Companion
import java.io.File
import java.nio.file.Path

object FileHelper {
    /**
     * Retrieves a file from the run directory.
     */
    fun file(path: String): File {
        return Path.of(path).toFile()
    }

    /**
     * Retrieves a file from the directory for Companion.
     */
    fun companionFile(path: String): File {
        return file("${Companion.MOD_ID}/$path")
    }
}

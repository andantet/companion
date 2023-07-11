package dev.andante.companion.extension

fun Regex.captureGroups(string: String, groups: Int): List<String> {
    val result = find(string) ?: return emptyList()
    val groupValues = result.groupValues
    return groupValues.subList(1, groups + 1)
}

fun Regex.captureGroup(string: String): String? {
    return captureGroups(string, 1).firstOrNull()
}

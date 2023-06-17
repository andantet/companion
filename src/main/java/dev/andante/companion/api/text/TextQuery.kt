package dev.andante.companion.api.text

import net.minecraft.text.LiteralTextContent
import net.minecraft.text.Text
import org.intellij.lang.annotations.RegExp
import java.util.function.Predicate

/**
 * A utility classs to find the first deep sibling of a [Text] thats [LiteralTextContent] matches `pattern`.
 */
data class TextQuery(val siblings: List<Text>, val index: Int) {
    val result: Text = siblings[index]

    fun getOffsetResult(offset: Int): Text {
        return siblings[index + offset]
    }

    companion object {
        fun findTexts(
            text: Text,
            predicate: Predicate<Text?>,
            pastSiblings: List<Text> = listOf(text),
            pastIndex: Int = 0
        ): List<TextQuery> {
            val queries: MutableList<TextQuery> = ArrayList()
            if (predicate.test(text)) {
                queries.add(TextQuery(pastSiblings, pastIndex))
            }
            val siblings = text.siblings
            var i = 0
            val l = siblings.size
            while (i < l) {
                val sibling = siblings[i]
                val subQueries = findTexts(sibling, predicate, siblings, i)
                queries.addAll(subQueries)
                i++
            }
            return queries
        }

        /**
         * @implNote does not use [findTexts] as this saves processing text when it doesn't have to
         */
        fun findText(
            text: Text,
            predicate: Predicate<Text>,
            pastSiblings: List<Text> = listOf(text),
            pastIndex: Int = 0
        ): TextQuery? {
            // test against predicate
            if (predicate.test(text)) {
                return TextQuery(pastSiblings, pastIndex)
            }

            val siblings = text.siblings
            var i = 0
            val l = siblings.size
            while (i < l) {
                val sibling = siblings[i]
                val maybeResult = findText(sibling, predicate, siblings, i)
                if (maybeResult != null) {
                    return maybeResult
                }
                i++
            }

            return null
        }

        fun findText(
            text: Text,
            @RegExp pattern: String,
            pastSiblings: List<Text> = listOf(text),
            pastIndex: Int = 0
        ): TextQuery? {
            return findText(text, { textx ->
                val content = textx.content
                if (content is LiteralTextContent) {
                    val raw: String = content.string()
                    raw.matches(pattern.toRegex())
                } else {
                    false
                }
            }, pastSiblings, pastIndex)
        }
    }
}

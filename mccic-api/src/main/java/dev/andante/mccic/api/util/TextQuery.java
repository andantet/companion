package dev.andante.mccic.api.util;

import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.Text;
import org.intellij.lang.annotations.RegExp;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Finds the first deep sibling of a {@link Text} thats {@link LiteralTextContent} matches {@code pattern}.
 */
public record TextQuery(List<Text> siblings, int index) {
    public static List<TextQuery> findTexts(Text text, Predicate<Text> predicate, List<Text> pastSiblings, int pastIndex) {
        List<TextQuery> queries = new ArrayList<>();

        if (predicate.test(text)) {
            queries.add(new TextQuery(pastSiblings, pastIndex));
        }

        List<Text> siblings = text.getSiblings();
        for (int i = 0, l = siblings.size(); i < l; i++) {
            Text sibling = siblings.get(i);
            List<TextQuery> subQueries = findTexts(sibling, predicate, siblings, i);
            queries.addAll(subQueries);
        }

        return queries;
    }

    public static List<TextQuery> findTexts(Text text, Predicate<Text> predicate) {
        return findTexts(text, predicate, List.of(text), 0);
    }

    /**
     * @implNote does not use {@link #findTexts(Text, Predicate, List, int)} as this saves processing text when it doesn't have to
     */
    public static Optional<TextQuery> findText(Text text, Predicate<Text> predicate, List<Text> pastSiblings, int pastIndex) {
        if (predicate.test(text)) {
            return Optional.of(new TextQuery(pastSiblings, pastIndex));
        }

        List<Text> siblings = text.getSiblings();
        for (int i = 0, l = siblings.size(); i < l; i++) {
            Text sibling = siblings.get(i);
            Optional<TextQuery> maybeResult = findText(sibling, predicate, siblings, i);
            if (maybeResult.isPresent()) {
                return maybeResult;
            }
        }

        return Optional.empty();
    }

    public static Optional<TextQuery> findText(Text text, Predicate<Text> predicate) {
        return findText(text, predicate, List.of(text), 0);
    }

    public static Optional<TextQuery> findText(Text text, @RegExp String pattern, List<Text> pastSiblings, int pastIndex) {
        return findText(text, textx -> {
            if (textx.getContent() instanceof LiteralTextContent content) {
                String raw = content.string();
                return raw.matches(pattern);
            }

            return false;
        }, pastSiblings, pastIndex);
    }

    public static Optional<TextQuery> findText(Text text, @RegExp String pattern) {
        return findText(text, pattern, List.of(text), 0);
    }

    public Text getResult() {
        return this.siblings.get(this.index);
    }

    public Text getOffsetResult(int offset) {
        return this.siblings.get(this.index + offset);
    }
}

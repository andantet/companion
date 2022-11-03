package dev.andante.mccic.api.util;

import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.Text;
import org.intellij.lang.annotations.RegExp;

import java.util.List;
import java.util.Optional;

/**
 * Finds the first deep sibling of a {@link Text} thats {@link LiteralTextContent} matches {@code pattern}.
 */
public record TextQuery(List<Text> siblings, int index) {
    public static Optional<TextQuery> findText(Text text, @RegExp String pattern, List<Text> pastSiblings, int pastIndex) {
        if (text.getContent() instanceof LiteralTextContent content) {
            String raw = content.string();
            if (raw.matches(pattern)) {
                return Optional.of(new TextQuery(pastSiblings, pastIndex));
            }
        }

        List<Text> siblings = text.getSiblings();
        for (int i = 0, l = siblings.size(); i < l; i++) {
            Text sibling = siblings.get(i);
            Optional<TextQuery> maybeResult = findText(sibling, pattern, siblings, i);
            if (maybeResult.isPresent()) {
                return maybeResult;
            }
        }

        return Optional.empty();
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

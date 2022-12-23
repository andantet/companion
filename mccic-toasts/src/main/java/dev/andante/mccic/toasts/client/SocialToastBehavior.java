package dev.andante.mccic.toasts.client;

import dev.andante.mccic.api.client.util.ClientHelper;
import dev.andante.mccic.api.util.TextQuery;
import dev.andante.mccic.toasts.client.toast.SocialToast.EventType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import org.intellij.lang.annotations.RegExp;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Environment(EnvType.CLIENT)
public class SocialToastBehavior {
    private final Pattern pattern;
    private final String backupUsername;
    private final BooleanSupplier condition;
    private final List<EventType> eventTypes;

    public SocialToastBehavior(Pattern pattern, String backupUsername, BooleanSupplier condition, EventType... eventTypes) {
        this.pattern = pattern;
        this.backupUsername = backupUsername;
        this.condition = condition;
        this.eventTypes = Arrays.asList(eventTypes);
    }

    public static SocialToastBehavior create(@RegExp String pattern, String backupUsername, BooleanSupplier condition, EventType... eventTypes) {
        return new SocialToastBehavior(Pattern.compile(pattern.formatted("(%s)".formatted(TextQuery.USERNAME_REGEX))), backupUsername, condition, eventTypes);
    }

    public static SocialToastBehavior create(@RegExp String pattern, BooleanSupplier condition, EventType... eventTypes) {
        return create(pattern, "", condition, eventTypes);
    }

    public static SocialToastBehavior createUncaptured(@RegExp String pattern, BooleanSupplier condition, EventType... eventTypes) {
        return new SocialToastBehavior(Pattern.compile(pattern.formatted(TextQuery.USERNAME_REGEX)), "", condition, eventTypes);
    }

    public static String getClientUsername() {
        return ClientHelper.getFromClient(MinecraftClient::getSession).getUsername();
    }

    public Optional<String> matchAndRetrieveUsername(String str) {
        Matcher matcher = this.pattern.matcher(str);
        if (matcher.matches()) {
            try {
                return Optional.of(matcher.group(1));
            } catch (IndexOutOfBoundsException ignored) {
            }

            return Optional.of(this.backupUsername);
        }

        return Optional.empty();
    }

    public List<EventType> getEventTypes() {
        return this.eventTypes;
    }

    public boolean shouldToast() {
        return this.condition.getAsBoolean();
    }
}

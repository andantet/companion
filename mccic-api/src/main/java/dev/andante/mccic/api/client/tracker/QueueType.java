package dev.andante.mccic.api.client.tracker;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum QueueType {
    NONE,
    QUICKPLAY,
    CASUAL("CASUAL");

    private static final Map<String, QueueType> TO_SCOREBOARD_NAME = Arrays.stream(QueueType.values())
                                                                           .filter(type -> type.getName() != null)
                                                                           .collect(Collectors.toMap(QueueType::getName, Function.identity()));

    private final String name;

    QueueType(String name) {
        this.name = name;
    }

    QueueType() {
        this(null);
    }

    @Nullable
    public static QueueType fromScoreboard(String name) {
        return TO_SCOREBOARD_NAME.getOrDefault(name.trim(), null);
    }

    public String getName() {
        return this.name;
    }
}

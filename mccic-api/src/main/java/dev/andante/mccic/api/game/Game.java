package dev.andante.mccic.api.game;

import dev.andante.mccic.api.MCCIC;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.TypedActionResult;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Game implements StringIdentifiable {
    HOLE_IN_THE_WALL("Hole in the Wall", "HOLE IN THE WALL"),
    TGTTOS("TGTTOS", "TGTTOS"),
    SKY_BATTLE("Sky Battle", "SKY BATTLE"),
    BATTLE_BOX("Battle Box", "BATTLE BOX");

    private static final Map<String, Game> TO_SCOREBOARD_NAME = Arrays.stream(Game.values())
                                                                      .collect(Collectors.toMap(Game::getScoreboardName, Function.identity()));

    private final String displayName, scoreboardName;
    private final Identifier soundId;

    Game(String displayName, String scoreboardName) {
        this.displayName = displayName;
        this.scoreboardName = scoreboardName;
        this.soundId = new Identifier("%s-music".formatted(MCCIC.MOD_ID), "game.%s".formatted(this.getId()));
    }

    @Nullable
    public static TypedActionResult<Game> fromScoreboard(String name) {
        String trim = name.trim();
        Game game = TO_SCOREBOARD_NAME.getOrDefault(trim, null);
        return trim.equals(name) ? TypedActionResult.success(game) : TypedActionResult.fail(game);
    }

    public String getId() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public String getScoreboardName() {
        return this.scoreboardName;
    }

    public Identifier getSoundId() {
        return this.soundId;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public String asString() {
        return this.displayName;
    }
}

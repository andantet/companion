package dev.andante.mccic.api.game;

import net.minecraft.util.TypedActionResult;

import java.util.HashMap;
import java.util.Map;

public final class GameRegistry {
    public static final GameRegistry INSTANCE = new GameRegistry();

    private final Map<String, Game> registry;
    private final Map<Game, String> reverseRegistry;

    private GameRegistry() {
        this.registry = new HashMap<>();
        this.reverseRegistry = new HashMap<>();
    }

    public <T extends Game> T register(String id, T game) {
        this.registry.put(id, game);
        this.reverseRegistry.put(game, id);
        return game;
    }

    public Game get(String id) {
        return this.registry.get(id);
    }

    public String getId(Game game) {
        return this.reverseRegistry.get(game);
    }

    public TypedActionResult<Game> fromScoreboard(String str) {
        String trim = str.trim();
        Game game = this.registry.values()
                                 .stream()
                                 .filter(gamex -> gamex.getScoreboardNames().contains(trim))
                                 .findAny()
                                 .orElse(null);
        return trim.equals(str) ? TypedActionResult.success(game) : TypedActionResult.fail(game);
    }
}

package dev.andante.mccic.api.client.game;

import dev.andante.mccic.api.game.Game;
import dev.andante.mccic.api.game.GameState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.OptionalInt;

/**
 * Tracks game active data.
 */
@Environment(EnvType.CLIENT)
public interface GameTracker {
    GameTracker INSTANCE = new EventsGameTracker();

    Game getGame();
    GameState getGameState();
    OptionalInt getTime();

    boolean isInGame();

    /**
     * Whether the client is connected to a server with
     * an IP address ending in <code>mccisland.net</code>.
     */
    boolean isOnServer();
}

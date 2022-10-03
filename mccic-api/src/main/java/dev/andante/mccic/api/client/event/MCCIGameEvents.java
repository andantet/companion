package dev.andante.mccic.api.client.event;

import dev.andante.mccic.api.game.Game;
import dev.andante.mccic.api.game.GameState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@Environment(EnvType.CLIENT)
public interface MCCIGameEvents {
    /**
     * Invoked when the active game changes.
     */
    Event<GameChange> GAME_CHANGE = EventFactory.createArrayBacked(GameChange.class, callbacks -> (game, oldGame) -> {
        for (GameChange event : callbacks) {
            event.onGameChange(game, oldGame);
        }
    });

    @Environment(EnvType.CLIENT)
    @FunctionalInterface
    interface GameChange {
        void onGameChange(Game game, Game oldGame);
    }

    /**
     * Invoked when the game's boss bar timer updates.
     */
    Event<TimerUpdate> TIMER_UPDATE = EventFactory.createArrayBacked(TimerUpdate.class, callbacks -> (time, lastTime) -> {
        for (TimerUpdate event : callbacks) {
            event.onTimerUpdate(time, lastTime);
        }
    });

    @Environment(EnvType.CLIENT)
    @FunctionalInterface
    interface TimerUpdate {
        void onTimerUpdate(int time, int lastTime);
    }

    /**
     * Invoked when the game's inferred state updates.
     */
    Event<StateUpdate> STATE_UPDATE = EventFactory.createArrayBacked(StateUpdate.class, callbacks -> (state, oldState) -> {
        for (StateUpdate event : callbacks) {
            event.onStateUpdate(state, oldState);
        }
    });

    @Environment(EnvType.CLIENT)
    @FunctionalInterface
    interface StateUpdate {
        void onStateUpdate(GameState state, GameState oldState);
    }
}

package dev.andante.mccic.api.game;

import java.util.List;

public class SkyBattleGame extends Game {
    public SkyBattleGame() {
    }

    @Override
    public List<String> getScoreboardNames() {
        return List.of("SKY BATTLE");
    }
}

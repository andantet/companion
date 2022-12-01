package dev.andante.mccic.api.game;

import java.util.List;

public class BattleBoxGame extends Game {
    public BattleBoxGame() {
    }

    @Override
    public List<String> getScoreboardNames() {
        return List.of("BATTLE BOX");
    }

    @Override
    public boolean hasTeamChat() {
        return true;
    }
}

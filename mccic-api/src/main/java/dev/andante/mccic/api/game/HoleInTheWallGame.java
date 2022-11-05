package dev.andante.mccic.api.game;

import java.util.List;

public class HoleInTheWallGame extends Game {
    public HoleInTheWallGame() {
    }

    @Override
    public List<String> getScoreboardNames() {
        return List.of("HOLE IN THE WALL");
    }
}

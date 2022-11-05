package dev.andante.mccic.api.game;

import java.util.List;

public class TGTTOSGame extends Game {
    public TGTTOSGame() {
    }

    @Override
    public List<String> getScoreboardNames() {
        return List.of("TGTTOS", "TO GET TO THE OTHER SIDE");
    }
}

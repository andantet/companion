package dev.andante.mccic.music;

import net.minecraft.util.Identifier;

public interface MCCICSounds {
    Identifier EARLY_ELIMINATION = create("early_elimination");
    Identifier TEAM_ELIMINATED = create("team_eliminated");
    Identifier SCORE_BIG_COINS = create("score.big_coins");
    Identifier SCORE_ACQUIRED = create("score.acquired");

    static Identifier create(String id) {
        return new Identifier("mccic-music", id);
    }
}

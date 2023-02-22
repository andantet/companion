package dev.andante.mccic.api.sound;

import dev.andante.mccic.api.MCCIC;
import net.minecraft.util.Identifier;

public interface MCCICSounds {
    Identifier EARLY_ELIMINATION = create("early_elimination");
    Identifier TEAM_ELIMINATED = create("team_eliminated");
    Identifier SCORE_BIG_COINS = create("score.big_coins");
    Identifier SCORE_ACQUIRED = create("score.acquired");
    Identifier UI_CLICK_NORMAL = create("ui.click_normal");

    Identifier MUSIC_OVERTIME_INTRO = createMcc("music.global.overtime_intro_music");
    Identifier MUSIC_OVERTIME_LOOP = createMcc("music.global.overtime_loop_music");

    static Identifier create(String id) {
        return new Identifier(MCCIC.MOD_ID, id);
    }

    static Identifier createMcc(String id) {
        return new Identifier("mcc", id);
    }
}

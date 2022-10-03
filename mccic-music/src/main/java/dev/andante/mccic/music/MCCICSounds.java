package dev.andante.mccic.music;

import net.minecraft.util.Identifier;

public interface MCCICSounds {
    Identifier EARLY_ELIMINATION = create("early_elimination");

    static Identifier create(String id) {
        return new Identifier("mccic-music", id);
    }
}

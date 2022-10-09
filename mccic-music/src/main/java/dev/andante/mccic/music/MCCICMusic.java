package dev.andante.mccic.music;

import dev.andante.mccic.api.MCCIC;

public interface MCCICMusic extends MCCIC {
    String ID = "music";
    String MOD_ID = "%s-%s".formatted(MCCIC.MOD_ID, ID);
}

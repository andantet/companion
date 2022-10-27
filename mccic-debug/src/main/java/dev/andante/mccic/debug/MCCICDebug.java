package dev.andante.mccic.debug;

import dev.andante.mccic.api.MCCIC;

public interface MCCICDebug extends MCCIC {
    String ID = "debug";
    String MOD_ID = MCCIC.createModId(ID);
}

package dev.andante.mccic.qol;

import dev.andante.mccic.api.MCCIC;

public interface MCCICQoL extends MCCIC {
    String ID = "qol";
    String MOD_ID = MCCIC.createModId(ID);
}

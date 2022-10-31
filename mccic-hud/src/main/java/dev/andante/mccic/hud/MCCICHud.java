package dev.andante.mccic.hud;

import dev.andante.mccic.api.MCCIC;

public interface MCCICHud extends MCCIC {
    String ID = "hud";
    String MOD_ID = MCCIC.createModId(ID);
}

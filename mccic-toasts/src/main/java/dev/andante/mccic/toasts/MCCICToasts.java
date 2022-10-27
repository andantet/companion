package dev.andante.mccic.toasts;

import dev.andante.mccic.api.MCCIC;

public interface MCCICToasts extends MCCIC {
    String ID = "toasts";
    String MOD_ID = MCCIC.createModId(ID);
}

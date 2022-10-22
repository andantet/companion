package dev.andante.mccic.config;

import dev.andante.mccic.api.MCCIC;

public interface MCCICConfig extends MCCIC {
    String ID = "config";
    String MOD_ID = "%s-%s".formatted(MCCIC.MOD_ID, ID);
}

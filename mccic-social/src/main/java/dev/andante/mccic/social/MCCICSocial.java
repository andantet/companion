package dev.andante.mccic.social;

import dev.andante.mccic.api.MCCIC;

public interface MCCICSocial extends MCCIC {
    String ID = "social";
    String MOD_ID = "%s-%s".formatted(MCCIC.MOD_ID, ID);
}

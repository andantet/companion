package dev.andante.mccic.api;

public interface MCCICApi extends MCCIC {
    String ID = "api";
    String MOD_ID = "%s-%s".formatted(MCCIC.MOD_ID, ID);
}

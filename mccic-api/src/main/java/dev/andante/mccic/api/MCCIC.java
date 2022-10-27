package dev.andante.mccic.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface MCCIC {
    String MOD_ID = "mccic";
    String MOD_NAME = "MCCI: Companion";
    Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    static String createModId(String id) {
        return "%s-%s".formatted(MCCIC.MOD_ID, id);
    }
}

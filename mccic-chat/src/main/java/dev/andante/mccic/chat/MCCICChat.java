package dev.andante.mccic.chat;

import dev.andante.mccic.api.MCCIC;

public interface MCCICChat extends MCCIC {
    String ID = "chat";
    String MOD_ID = MCCIC.createModId(ID);
}

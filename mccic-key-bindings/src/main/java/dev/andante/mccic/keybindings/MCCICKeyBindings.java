package dev.andante.mccic.keybindings;

import dev.andante.mccic.api.MCCIC;

public interface MCCICKeyBindings extends MCCIC {
    String ID = "key-bindings";
    String MOD_ID = MCCIC.createModId(ID);
}

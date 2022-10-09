package dev.andante.mccic.discordrp;

import dev.andante.mccic.api.MCCIC;

public interface MCCICDiscordRP extends MCCIC {
    String ID = "discord-rp";
    String MOD_ID = "%s-%s".formatted(MCCIC.MOD_ID, ID);
}

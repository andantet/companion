package dev.andante.mccic.api.util;

import net.minecraft.util.StringIdentifiable;

public enum ChatMode implements StringIdentifiable {
    LOCAL("local"),
    PARTY("party"),
    TEAM("team");

    private final String id;

    ChatMode(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    @Override
    public String asString() {
        return this.id;
    }
}

package dev.andante.mccic.api.util;

import net.minecraft.util.StringIdentifiable;

public enum ChatMode implements StringIdentifiable {
    LOCAL("local"),
    PARTY("party");

    private final String id;

    ChatMode(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public ChatMode next() {
        ChatMode[] values = values();
        return values[(this.ordinal() + 1) % values.length];
    }

    @Override
    public String asString() {
        return this.id;
    }
}

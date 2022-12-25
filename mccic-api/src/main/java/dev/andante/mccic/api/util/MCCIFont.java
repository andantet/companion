package dev.andante.mccic.api.util;

import net.minecraft.util.Identifier;

/**
 * Selected declared fonts used on MCC: Island.
 */
public enum MCCIFont {
    HUD("hud"),
    GUI("gui");

    private final String id;
    private final Identifier font;

    MCCIFont(String id) {
        this.id = id;
        this.font = new Identifier("mcc", id);
    }

    public String getId() {
        return this.id;
    }

    public Identifier getFont() {
        return this.font;
    }
}

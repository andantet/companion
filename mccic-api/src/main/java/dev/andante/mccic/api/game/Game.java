package dev.andante.mccic.api.game;

import dev.andante.mccic.api.MCCIC;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;

import java.util.List;

public abstract class Game implements StringIdentifiable {
    private String translationKey;

    public Game() {
    }

    public String getOrCreateTranslationKey() {
        if (this.translationKey == null) {
            this.translationKey = "%s.game.%s".formatted(MCCIC.MOD_ID, GameRegistry.INSTANCE.getId(this));
        }

        return this.translationKey;
    }

    public MutableText getDisplayName() {
        return Text.translatable(this.getOrCreateTranslationKey());
    }

    public String getDisplayString() {
        return this.getDisplayName().getString();
    }

    public abstract List<String> getScoreboardNames();

    @Override
    public String asString() {
        return GameRegistry.INSTANCE.getId(this);
    }

    @Override
    public String toString() {
        return "Game{" + GameRegistry.INSTANCE.getId(this) + "}";
    }
}

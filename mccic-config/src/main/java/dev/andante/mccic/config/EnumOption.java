package dev.andante.mccic.config;

import dev.andante.mccic.config.client.screen.AbstractConfigScreen;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.TranslatableOption;

public interface EnumOption extends StringIdentifiable, TranslatableOption {
    String getEnumIdentifier();
    String getIdentifier();
    String getModId();

    @Override
    default String asString() {
        return this.getIdentifier();
    }

    @Override
    default String getTranslationKey() {
        return AbstractConfigScreen.createConfigTranslationKey(this.getModId(), "%s.%s".formatted(this.getEnumIdentifier(), this.getIdentifier()));
    }
}

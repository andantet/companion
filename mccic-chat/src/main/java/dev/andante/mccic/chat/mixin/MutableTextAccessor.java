package dev.andante.mccic.chat.mixin;

import net.minecraft.text.MutableText;
import net.minecraft.text.TextContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MutableText.class)
public interface MutableTextAccessor {
    @Mutable @Accessor void setContent(TextContent content);
}

package dev.andante.mccic.config.mixin.client;

import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameOptions.class)
public interface GameOptionsInvoker {
    @Invoker static Text invokeGetPercentValueText(Text prefix, double value) { throw new AssertionError(); }
}

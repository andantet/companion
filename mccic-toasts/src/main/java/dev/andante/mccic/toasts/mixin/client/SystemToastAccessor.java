package dev.andante.mccic.toasts.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.SystemToast.Type;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(SystemToast.class)
public interface SystemToastAccessor {
    @Accessor long getStartTime();
    @Accessor void setStartTime(long startTime);

    @Accessor boolean isJustUpdated();
    @Accessor void setJustUpdated(boolean justUpdated);

    @Accessor List<OrderedText> getLines();
    @Accessor void setLines(List<OrderedText> lines);

    @Accessor Type getType();
    @Accessor Text getTitle();
}

package dev.andante.mccic.chat.client;

import dev.andante.mccic.chat.mixin.client.ChatHudLineVisibleMixin;
import dev.andante.mccic.chat.mixin.client.ChatHudMixin;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.OrderedText;

/**
 * Interface access for {@link ChatHudLine.Visible}.
 * @see ChatHudLineVisibleMixin
 * @see ChatHudMixin
 */
@Environment(EnvType.CLIENT)
public interface ChatHudLineVisibleAccess {
    default void mccic_setMentionedText(OrderedText text) {
        throw new AssertionError();
    }
}

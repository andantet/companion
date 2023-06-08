package dev.andante.mccic.qol.client.disconnect;

import dev.andante.mccic.qol.MCCICQoL;
import dev.andante.mccic.qol.mixin.client.GameMenuScreenMixin;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget.PressAction;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

/**
 * Replaces the disconnect action when attempting to leave a server.
 * @see GameMenuScreenMixin
 */
@Environment(EnvType.CLIENT)
public class ConfirmDisconnectScreen extends Screen {
    public static final Text DISCONNECT_TEXT = Text.translatable("menu.disconnect");
    public static final Text CANCEL_TEXT = Text.translatable("gui.cancel");
    public static final Text CONFIRM_TEXT = Text.translatable("menu.%s.confirmDisconnect".formatted(MCCICQoL.MOD_ID));

    public static final int BOX_WIDTH = 120;
    public static final int BOX_HEIGHT = 20;

    private final Screen parent;
    private final PressAction pressAction;

    public ConfirmDisconnectScreen(Screen parent, PressAction pressAction) {
        super(Text.translatable("menu.disconnect"));
        this.parent = parent;
        this.pressAction = pressAction;
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    @Override
    protected void init() {
        int x = (this.width / 2) - (BOX_WIDTH / 2);
        int y = (this.height / 2) - BOX_HEIGHT;
        this.addDrawableChild(ButtonWidget.builder(DISCONNECT_TEXT, pressAction).dimensions(x, y, BOX_WIDTH, BOX_HEIGHT).build());
        this.addDrawableChild(ButtonWidget.builder(CANCEL_TEXT, button -> this.close()).dimensions(x, y + BOX_HEIGHT + (BOX_HEIGHT / 4) + 5 + 6, BOX_WIDTH, BOX_HEIGHT).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);

        int x = (int) ((this.width / 2f) - (this.textRenderer.getWidth(CONFIRM_TEXT) / 2f));
        int y = (int) ((this.height / 2f) - BOX_HEIGHT - (BOX_HEIGHT / 2f) - (BOX_HEIGHT / 4f));
        context.drawTextWithShadow(this.textRenderer, CONFIRM_TEXT, x, y, 0xFFFFFFFF);
    }
}

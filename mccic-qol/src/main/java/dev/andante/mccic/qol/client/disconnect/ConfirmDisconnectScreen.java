package dev.andante.mccic.qol.client.disconnect;

import dev.andante.mccic.api.MCCIC;
import dev.andante.mccic.qol.mixin.client.GameMenuScreenMixin;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
    public static final Text CONFIRM_TEXT = Text.translatable("menu.%s.confirmDisconnect".formatted(MCCIC.MOD_ID));

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
        int center = this.width / 2;
        int y = (this.height / 2) + 5;
        this.addDrawableChild(new ButtonWidget(center - BOX_WIDTH - 6, y, BOX_WIDTH, BOX_HEIGHT, DISCONNECT_TEXT, pressAction));
        this.addDrawableChild(new ButtonWidget(center + 6, y, BOX_WIDTH, BOX_HEIGHT, CANCEL_TEXT, button -> this.close()));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.textRenderer.draw(matrices, CONFIRM_TEXT, (int) ((this.width / 2f) - (this.textRenderer.getWidth(CONFIRM_TEXT) / 2f)), (int) (this.height / 2f) - 20, 0xFFFFFFFF);
    }
}

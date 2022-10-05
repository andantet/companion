package dev.andante.mccic.config.client.screen;

import dev.andante.mccic.api.MCCIC;
import dev.andante.mccic.api.client.toast.MCCICToast;
import dev.andante.mccic.config.ConfigHelper;
import dev.andante.mccic.config.ConfigHolder;
import dev.andante.mccic.config.client.ClientConfigRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class MCCICConfigScreen extends Screen {
    public static final Identifier RELOAD_ICONS_TEXTURE = new Identifier("%s-config".formatted(MCCIC.MOD_ID), "textures/gui/reload_icons.png");

    public static final Text TITLE_TEXT = Text.translatable("ui.%s.config".formatted(MCCIC.MOD_ID));
    public static final Text RELOAD_TOOLTIP_TEXT = Text.translatable("ui.%s.config.reload.tooltip".formatted(MCCIC.MOD_ID));

    private final Screen parent;

    public MCCICConfigScreen(Screen parent) {
        super(TITLE_TEXT);
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.addDrawableChild(new TexturedButtonWidget(10, this.height - 10 - 20, 20, 20, 0, 0, 20, RELOAD_ICONS_TEXTURE, 32, 64, this::onReloadPress, this::renderReloadTooltip, RELOAD_TOOLTIP_TEXT));

        int width = 102;
        this.addDrawableChild(new ButtonWidget((int) ((this.width / 2f) - (width / 2f)), this.height - 60, width, 20, Text.translatable("gui.back"), button -> this.close()));
    }

    protected void onReloadPress(ButtonWidget button) {
        ClientConfigRegistry.INSTANCE.forEach(ConfigHolder::load);
        this.client.getToastManager().add(new MCCICToast(ConfigHelper.RELOAD_TITLE_TEXT, ConfigHelper.RELOAD_DESCRIPTION_TEXT));
        this.close();
    }

    protected void renderReloadTooltip(ButtonWidget button, MatrixStack matrices, int mouseX, int mouseY) {
        this.renderTooltip(matrices, RELOAD_TOOLTIP_TEXT, mouseX, mouseY);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.textRenderer.draw(matrices, this.title, (int) ((this.width / 2f) - (this.textRenderer.getWidth(this.title) / 2f)), 60, 0xFFFFFFFF);
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}

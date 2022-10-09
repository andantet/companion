package dev.andante.mccic.config.client.screen;

import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.Codec;
import dev.andante.mccic.api.MCCIC;
import dev.andante.mccic.api.client.game.GameTracker;
import dev.andante.mccic.api.client.toast.MCCICToast;
import dev.andante.mccic.config.ConfigHelper;
import dev.andante.mccic.config.ConfigHolder;
import dev.andante.mccic.config.mixin.client.GameOptionsInvoker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.TranslatableOption;

import java.util.Arrays;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public abstract class MCCICAbstractConfigScreen<T extends Record> extends Screen {
    public static final Identifier RELOAD_ICONS_TEXTURE = new Identifier("%s-config".formatted(MCCIC.MOD_ID), "textures/gui/reload_icons.png");
    public static final Identifier RELOAD_ICONS_TEXTURE_MCCI = new Identifier("mcci", RELOAD_ICONS_TEXTURE.getPath());

    public static final String CONFIG_RELOAD_TEXT_KEY = "ui.%s.config.reload".formatted(MCCIC.MOD_ID);
    public static final Text RELOAD_TOOLTIP_TEXT = Text.translatable("%s.tooltip".formatted(CONFIG_RELOAD_TEXT_KEY));

    public static final int TITLE_Y = 60;
    public static final int BACK_BUTTON_WIDTH = 102;
    public static final int BACK_BUTTON_HEIGHT = 20;
    public static final int SQUARE_BUTTON_SIZE = 20;

    protected final Screen parent;
    protected final boolean hasConfiguration;
    protected final ConfigHolder<T> configHolder;

    protected ButtonListWidget list;

    public MCCICAbstractConfigScreen(Text title, Screen parent) {
        super(title);
        this.parent = parent;
        this.hasConfiguration = false;
        this.configHolder = null;
    }

    public MCCICAbstractConfigScreen(String modId, Screen parent, ConfigHolder<T> configHolder) {
        super(Text.translatable("ui.%s.config".formatted(modId)));
        this.parent = parent;
        this.hasConfiguration = true;
        this.configHolder = configHolder;
    }

    @Override
    protected void init() {
        // back button
        this.addDrawableChild(new ButtonWidget(
            (int) ((this.width / 2f) - (BACK_BUTTON_WIDTH / 2f)), this.height - 60,
            BACK_BUTTON_WIDTH, BACK_BUTTON_HEIGHT, ScreenTexts.BACK, this::onBackButton
        ));

        // reload button
        if (!this.hasConfiguration) {
            this.addDrawableChild(new TexturedButtonWidget(
                10, this.height - 10 - SQUARE_BUTTON_SIZE, SQUARE_BUTTON_SIZE, SQUARE_BUTTON_SIZE, 0, 0, SQUARE_BUTTON_SIZE,
                GameTracker.INSTANCE.isOnServer() ? RELOAD_ICONS_TEXTURE_MCCI : RELOAD_ICONS_TEXTURE,
                32, 64, this::onReloadButton, this::renderReloadTooltip, RELOAD_TOOLTIP_TEXT
            ));
        }

        // options list
        this.list = new ButtonListWidget(this.client, this.width, this.height, 93, this.height - 94, 25);
        this.list.setRenderBackground(false);
        this.list.setRenderHorizontalShadows(false);
        this.addSelectableChild(this.list);
    }

    protected void onBackButton(ButtonWidget widget) {
        this.close();
    }

    protected void onReloadButton(ButtonWidget button) {
        this.reloadConfig();
        this.client.getToastManager().add(new MCCICToast(this.getConfigReloadTitleText(), ConfigHelper.RELOAD_DESCRIPTION_TEXT));
    }

    protected Text getConfigReloadTitleText() {
        return this.hasConfiguration ? Text.translatable(CONFIG_RELOAD_TEXT_KEY, this.title) : ConfigHelper.RELOAD_TITLE_TEXT;
    }

    protected void reloadConfig() {
        this.configHolder.load();
    }

    protected void renderReloadTooltip(ButtonWidget button, MatrixStack matrices, int mouseX, int mouseY) {
        this.renderTooltip(matrices, RELOAD_TOOLTIP_TEXT, mouseX, mouseY);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        // background
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);

        // title
        int padding = 6;
        drawOpaqueBlack(0, TITLE_Y - padding, this.width, TITLE_Y + this.textRenderer.fontHeight + padding);

        this.textRenderer.draw(matrices, this.title, (int) ((this.width / 2f) - (this.textRenderer.getWidth(this.title) / 2f)), TITLE_Y, 0xFFFFFFFF);

        // options
        if (!this.list.children().isEmpty()) {
            this.list.render(matrices, mouseX, mouseY, delta);
        }
    }

    public static void drawOpaqueBlack(int x1, int y1, int x2, int y2) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableTexture();
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA, SrcFactor.ZERO, DstFactor.ONE);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        int alpha = (int) (255 * 0.5F);
        buffer.begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(x1, y1, 0).color(0, 0, 0, alpha).next();
        buffer.vertex(x1, y2, 0).color(0, 0, 0, alpha).next();
        buffer.vertex(x2, y2, 0).color(0, 0, 0, alpha).next();
        buffer.vertex(x2, y1, 0).color(0, 0, 0, alpha).next();
        tessellator.draw();
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return super.mouseReleased(mouseX, mouseY, button) || this.list.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
        this.saveConfig();
    }

    protected void saveConfig() {
        this.configHolder.save();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    public static <T extends Enum<T> & TranslatableOption> SimpleOption<T> ofEnum(String modId, String id, Function<Integer, T> fromId, T[] values, T defaultValue) {
        return new SimpleOption<>(
            createConfigTranslationKey(modId, id),
            SimpleOption.emptyTooltip(), SimpleOption.enumValueText(),
            new SimpleOption.PotentialValuesBasedCallbacks<>(
                Arrays.asList(values),
                Codec.INT.xmap(fromId, T::ordinal)
            ), defaultValue, value -> {}
        );
    }

    public static SimpleOption<Boolean> ofBoolean(String modId, String id, boolean defaultValue) {
        return SimpleOption.ofBoolean(createConfigTranslationKey(modId, id), SimpleOption.emptyTooltip(), defaultValue);
    }

    public static SimpleOption<Double> ofDouble(String modId, String id, double defaultValue) {
        return new SimpleOption<>(createConfigTranslationKey(modId, id), SimpleOption.emptyTooltip(), (text, value) -> {
            return value == 0.0 ? GameOptions.getGenericValueText(text, ScreenTexts.OFF) : GameOptionsInvoker.invokeGetPercentValueText(text, value);
        }, SimpleOption.DoubleSliderCallbacks.INSTANCE, defaultValue, value -> {});
    }

    public static String createConfigTranslationKey(String modId, String id) {
        return "config.%s.%s".formatted(modId, id);
    }
}

package dev.andante.mccic.config.client.screen;

import com.mojang.serialization.Codec;
import dev.andante.mccic.api.client.toast.CustomToast;
import dev.andante.mccic.api.client.tracker.GameTracker;
import dev.andante.mccic.api.client.util.ClientHelper;
import dev.andante.mccic.config.ConfigHelper;
import dev.andante.mccic.config.ConfigHolder;
import dev.andante.mccic.config.MCCICConfig;
import dev.andante.mccic.config.mixin.client.GameOptionsInvoker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.VideoOptionsScreen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.option.SimpleOption.TooltipFactoryGetter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.TranslatableOption;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public abstract class AbstractConfigScreen<T extends Record> extends Screen {
    public static final Identifier RELOAD_ICONS_TEXTURE = new Identifier(MCCICConfig.MOD_ID, "textures/gui/reload_icons.png");
    public static final Identifier RELOAD_ICONS_TEXTURE_MCCI = new Identifier("mcci", RELOAD_ICONS_TEXTURE.getPath());

    public static final String CONFIG_RELOAD_TEXT_KEY = "ui.%s.reload".formatted(MCCICConfig.MOD_ID);
    public static final Text RELOAD_TOOLTIP_TEXT = Text.translatable("%s.tooltip".formatted(CONFIG_RELOAD_TEXT_KEY));
    public static final Text SAVE_TEXT = Text.translatable("gui.%s.save".formatted(MCCICConfig.MOD_ID));

    public static final int TITLE_Y = 60;
    public static final int BACK_BUTTON_WIDTH = 102;
    public static final int BACK_BUTTON_HEIGHT = 20;
    public static final int SQUARE_BUTTON_SIZE = 20;

    protected final Screen parent;
    protected final boolean hasConfiguration;
    protected final ConfigHolder<T> configHolder;

    protected ButtonListWidget list;

    public AbstractConfigScreen(Text title, Screen parent) {
        super(title);
        this.parent = parent;
        this.hasConfiguration = false;
        this.configHolder = null;
    }

    public AbstractConfigScreen(String modId, Screen parent, ConfigHolder<T> configHolder) {
        super(Text.translatable("ui.%s.config".formatted(modId)));
        this.parent = parent;
        this.hasConfiguration = true;
        this.configHolder = configHolder;
    }

    @Override
    protected void init() {
        // options list
        this.list = new ButtonListWidget(this.client, this.width, this.height, 93, this.height - 94, 25);
        this.list.setRenderBackground(false);
        this.list.setRenderHorizontalShadows(false);
        this.getOptions().forEach(this.list::addSingleOptionEntry);
        this.addSelectableChild(this.list);

        // back button
        this.addDrawableChild(new ButtonWidget(
            (int) ((this.width / 2f) - (BACK_BUTTON_WIDTH / 2f)), this.height - 60,
            BACK_BUTTON_WIDTH, BACK_BUTTON_HEIGHT, this.hasConfiguration ? SAVE_TEXT : ScreenTexts.BACK, this::onBackButton
        ));

        // reload button
        if (!this.hasConfiguration) {
            this.addDrawableChild(new TexturedButtonWidget(
                10, this.height - 10 - SQUARE_BUTTON_SIZE, SQUARE_BUTTON_SIZE, SQUARE_BUTTON_SIZE, 0, 0, SQUARE_BUTTON_SIZE,
                GameTracker.INSTANCE.isOnServer() ? RELOAD_ICONS_TEXTURE_MCCI : RELOAD_ICONS_TEXTURE,
                32, 64, this::onReloadButton, this::renderReloadTooltip, RELOAD_TOOLTIP_TEXT
            ));
        }
    }

    protected List<SimpleOption<?>> getOptions() {
        return Collections.emptyList();
    }

    protected void onBackButton(ButtonWidget widget) {
        this.close();
    }

    protected void onReloadButton(ButtonWidget button) {
        this.reloadConfig();
        this.close();
        new CustomToast(this.getConfigReloadTitleText(), ConfigHelper.RELOAD_DESCRIPTION_TEXT).add();
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
        ClientHelper.drawOpaqueBlack(0, TITLE_Y - padding, this.width, TITLE_Y + this.textRenderer.fontHeight + padding);

        this.textRenderer.draw(matrices, this.title, (int) ((this.width / 2f) - (this.textRenderer.getWidth(this.title) / 2f)), TITLE_Y, 0xFFFFFFFF);

        // options
        if (!this.list.children().isEmpty()) {
            this.list.render(matrices, mouseX, mouseY, delta);
            this.renderOrderedTooltip(matrices, VideoOptionsScreen.getHoveredButtonTooltip(this.list, mouseX, mouseY), mouseX, mouseY);
        }
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
        this.configHolder.set(this.createConfig());
        this.configHolder.save();
    }

    public abstract T createConfig();

    @Override
    public boolean shouldPause() {
        return false;
    }

    public static <T extends Enum<T> & TranslatableOption> SimpleOption<T> ofEnum(String modId, String id, Function<Integer, T> fromId, T[] values, T value, T defaultValue) {
        return ofEnum(modId, id, fromId, values, value, defaultValue, SimpleOption.emptyTooltip(), v -> {});
    }

    public static <T extends Enum<T> & TranslatableOption> SimpleOption<T> ofEnum(String modId, String id, Function<Integer, T> fromId, T[] values, T value, T defaultValue, TooltipFactoryGetter<T> tooltipFactory, Consumer<T> changeCallback) {
        SimpleOption<T> option = new SimpleOption<>(
            createConfigTranslationKey(modId, id),
            tooltipFactory, SimpleOption.enumValueText(),
            new SimpleOption.PotentialValuesBasedCallbacks<>(
                Arrays.asList(values),
                Codec.INT.xmap(fromId, T::ordinal)
            ), defaultValue, changeCallback
        );
        option.setValue(value);
        return option;
    }

    public static SimpleOption<Boolean> ofBoolean(String modId, String id, boolean value, boolean defaultValue) {
        return ofBoolean(modId, id, value, defaultValue, SimpleOption.emptyTooltip(), v -> {});
    }

    public static SimpleOption<Boolean> ofBoolean(String modId, String id, boolean value, boolean defaultValue, TooltipFactoryGetter<Boolean> tooltipFactory) {
        return ofBoolean(modId, id, value, defaultValue, tooltipFactory, v -> {});
    }

    public static SimpleOption<Boolean> ofBoolean(String modId, String id, boolean value, boolean defaultValue, TooltipFactoryGetter<Boolean> tooltipFactory, Consumer<Boolean> changeCallback) {
        SimpleOption<Boolean> option = SimpleOption.ofBoolean(createConfigTranslationKey(modId, id), tooltipFactory, defaultValue, changeCallback);
        option.setValue(value);
        return option;
    }

    public static SimpleOption<Double> ofDouble(String modId, String id, double value, double defaultValue) {
        SimpleOption<Double> option = new SimpleOption<>(createConfigTranslationKey(modId, id), SimpleOption.emptyTooltip(), (text, val) -> {
            return val == 0.0 ? GameOptions.getGenericValueText(text, ScreenTexts.OFF) : GameOptionsInvoker.invokeGetPercentValueText(text, val);
        }, SimpleOption.DoubleSliderCallbacks.INSTANCE, defaultValue, val -> {});
        option.setValue(value);
        return option;
    }

    public static String createConfigTranslationKey(String modId, String id) {
        return "config.%s.%s".formatted(modId, id);
    }
}

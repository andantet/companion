package dev.andante.mccic.config.client.screen;

import com.mojang.datafixers.util.Either;
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
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionListWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.option.SimpleOption.TooltipFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.TranslatableOption;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
    private final String modId;
    protected final boolean hasConfiguration;
    protected final ConfigHolder<T> configHolder;

    protected OptionListWidget list;

    public AbstractConfigScreen(Text title, Screen parent) {
        super(title);
        this.parent = parent;
        this.modId = null;
        this.hasConfiguration = false;
        this.configHolder = null;
    }

    public AbstractConfigScreen(String modId, Screen parent, ConfigHolder<T> configHolder) {
        super(Text.translatable("ui.%s.config".formatted(modId)));
        this.parent = parent;
        this.modId = modId;
        this.hasConfiguration = true;
        this.configHolder = configHolder;
    }

    @Override
    protected void init() {
        // options list
        this.list = new OptionListWidget(this.client, this.width, this.height, 93, this.height - 94, 25);
        this.list.setRenderBackground(false);
        this.list.setRenderHorizontalShadows(false);
        this.getOptions().forEach(this.list::addSingleOptionEntry);

        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            this.getDevelopmentOptions().forEach(this.list::addSingleOptionEntry);
        }

        this.addSelectableChild(this.list);

        // back button
        this.addDrawableChild(ButtonWidget.builder(this.hasConfiguration ? SAVE_TEXT : ScreenTexts.BACK, this::onBackButton).dimensions(
                (int) ((this.width / 2f) - (BACK_BUTTON_WIDTH / 2f)), this.height - 60,
                BACK_BUTTON_WIDTH, BACK_BUTTON_HEIGHT
        ).build());

        // reload button
        if (!this.hasConfiguration) {
            ButtonWidget widget = this.addDrawableChild(new TexturedButtonWidget(
                10, this.height - 10 - SQUARE_BUTTON_SIZE, SQUARE_BUTTON_SIZE, SQUARE_BUTTON_SIZE, 0, 0, SQUARE_BUTTON_SIZE,
                GameTracker.INSTANCE.isOnServer() ? RELOAD_ICONS_TEXTURE_MCCI : RELOAD_ICONS_TEXTURE,
                32, 64, this::onReloadButton, RELOAD_TOOLTIP_TEXT
            ));
            widget.setTooltip(Tooltip.of(RELOAD_TOOLTIP_TEXT));
        }
    }

    protected List<SimpleOption<?>> getOptions() {
        return Collections.emptyList();
    }

    protected List<SimpleOption<?>> getDevelopmentOptions() {
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
        this.configHolder.load();
        this.configHolder.set(this.createConfig());
        this.configHolder.save();
    }

    public abstract T createConfig();

    public abstract T getConfig();
    public abstract T getDefaultConfig();

    @Override
    public boolean shouldPause() {
        return false;
    }

    public <E extends Enum<E> & TranslatableOption> SimpleOption<E> ofEnum(String id, Function<Integer, E> fromId, E[] values, Function<T, E> valueGetter) {
        return this.ofEnum(id, fromId, values, valueGetter, SimpleOption.emptyTooltip(), v -> {});
    }

    public <E extends Enum<E> & TranslatableOption> SimpleOption<E> ofEnum(String id, Function<Integer, E> fromId, E[] values, Function<T, E> valueGetter, TooltipFactory<E> tooltipFactory, Consumer<E> changeCallback) {
        SimpleOption<E> option = new SimpleOption<>(
            createConfigTranslationKey(id),
            tooltipFactory, SimpleOption.enumValueText(),
            new SimpleOption.PotentialValuesBasedCallbacks<>(
                Arrays.asList(values),
                Codec.INT.xmap(fromId, E::ordinal)
            ), valueGetter.apply(this.getDefaultConfig()), changeCallback
        );
        option.setValue(valueGetter.apply(this.getConfig()));
        return option;
    }

    public SimpleOption<Boolean> ofBoolean(String id, Function<T, Boolean> valueGetter) {
        return this.ofBoolean(id, valueGetter, SimpleOption.emptyTooltip(), v -> {});
    }

    public SimpleOption<Boolean> ofBoolean(String id, Function<T, Boolean> valueGetter, TooltipFactory<Boolean> tooltipFactory) {
        return this.ofBoolean(id, valueGetter, tooltipFactory, v -> {});
    }

    public SimpleOption<Boolean> ofBooleanTooltip(String id, Function<T, Boolean> valueGetter) {
        return this.ofBoolean(id, valueGetter, SimpleOption.constantTooltip(Text.translatable(this.createConfigTranslationKey(id + ".tooltip"))));
    }

    public SimpleOption<Boolean> ofBoolean(String id, Function<T, Boolean> valueGetter, TooltipFactory<Boolean> tooltipFactory, Consumer<Boolean> changeCallback) {
        SimpleOption<Boolean> option = SimpleOption.ofBoolean(createConfigTranslationKey(id), tooltipFactory, valueGetter.apply(this.getDefaultConfig()), changeCallback);
        option.setValue(valueGetter.apply(this.getConfig()));
        return option;
    }

    public SimpleOption<Double> ofDouble(String id, Function<T, Double> valueGetter) {
        SimpleOption<Double> option = new SimpleOption<>(createConfigTranslationKey(id), SimpleOption.emptyTooltip(), (text, val) -> {
            return val == 0.0 ? GameOptions.getGenericValueText(text, ScreenTexts.OFF) : GameOptionsInvoker.invokeGetPercentValueText(text, val);
        }, SimpleOption.DoubleSliderCallbacks.INSTANCE, valueGetter.apply(this.getDefaultConfig()), val -> {});
        option.setValue(valueGetter.apply(this.getConfig()));
        return option;
    }

    public SimpleOption<Float> ofFloat(String id, Function<T, Float> valueGetter) {
        SimpleOption<Float> option = new SimpleOption<>(createConfigTranslationKey(id), SimpleOption.emptyTooltip(), (text, val) -> {
            return val == 0.0 ? GameOptions.getGenericValueText(text, ScreenTexts.OFF) : GameOptionsInvoker.invokeGetPercentValueText(text, val);
        }, FloatSliderCallbacks.INSTANCE, valueGetter.apply(this.getDefaultConfig()), val -> {});
        option.setValue(valueGetter.apply(this.getConfig()));
        return option;
    }

    public String createConfigTranslationKey(String id) {
        return createConfigTranslationKey(this.modId, id);
    }

    public static String createConfigTranslationKey(String modId, String id) {
        return "config.%s.%s".formatted(modId, id);
    }

    @Environment(EnvType.CLIENT)
    public enum FloatSliderCallbacks implements SimpleOption.SliderCallbacks<Float> {
        INSTANCE;


        @Override
        public Optional<Float> validate(Float value) {
            return value >= 0.0 && value <= 1.0 ? Optional.of(value) : Optional.empty();
        }

        @Override
        public double toSliderProgress(Float value) {
            return value;
        }

        @Override
        public Float toValue(double progress) {
            return (float) progress;
        }

        @Override
        public Codec<Float> codec() {
            return Codec.either(Codec.floatRange(0.0F, 1.0F), Codec.BOOL)
                        .xmap(either -> either.map(value -> value, value -> value ? 1.0F : 0.0F), Either::left);
        }
    }
}

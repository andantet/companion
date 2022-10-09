package dev.andante.mccic.config.client.screen;

import dev.andante.mccic.api.MCCIC;
import dev.andante.mccic.config.ConfigHolder;
import dev.andante.mccic.config.client.ClientConfigRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.List;

@Environment(EnvType.CLIENT)
public class MCCICConfigScreen extends MCCICAbstractConfigScreen<Record> {
    public static final String OTHER_CONFIG_LOCATION_TEXT_KEY = "ui.%s.config.other_config_location.line".formatted(MCCIC.MOD_ID);
    public static final Text OTHER_CONFIG_LOCATION_TEXT_LINE0 = Text.translatable(OTHER_CONFIG_LOCATION_TEXT_KEY + "0");
    public static final Text OTHER_CONFIG_LOCATION_TEXT_LINE1 = Text.translatable(OTHER_CONFIG_LOCATION_TEXT_KEY + "1");
    public static final List<Text> OTHER_CONFIG_LOCATION_TEXTS = List.of(OTHER_CONFIG_LOCATION_TEXT_LINE0, OTHER_CONFIG_LOCATION_TEXT_LINE1);

    public MCCICConfigScreen(Screen parent) {
        super(Text.translatable("ui.%s.config".formatted(MCCIC.MOD_ID)), parent);
    }

    public MCCICConfigScreen() {
        this(null);
    }

    @Override
    protected void saveConfig() {
        ClientConfigRegistry.INSTANCE.forEach(ConfigHolder::save);
    }

    @Override
    protected void reloadConfig() {
        ClientConfigRegistry.INSTANCE.forEach(ConfigHolder::load);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        int l = OTHER_CONFIG_LOCATION_TEXTS.size();
        int padding = 6;
        int y = (int) (this.height / 2f) - this.textRenderer.fontHeight;
        drawOpaqueBlack(0, y - padding, this.width, y + (this.textRenderer.fontHeight * l) + padding);

        for (int i = 0; i < l; i++) {
            Text text = OTHER_CONFIG_LOCATION_TEXTS.get(i);
            this.textRenderer.draw(matrices, text,
                (int) ((this.width / 2f) - (this.textRenderer.getWidth(text) / 2f)),
                y + ((this.textRenderer.fontHeight + 1) * i),
                0xFFFFFFFF
            );
        }
    }
}

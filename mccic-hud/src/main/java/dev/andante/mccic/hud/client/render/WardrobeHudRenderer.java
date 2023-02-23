package dev.andante.mccic.hud.client.render;

import dev.andante.mccic.api.client.UnicodeIconsStore;
import dev.andante.mccic.api.client.UnicodeIconsStore.Icon;
import dev.andante.mccic.api.client.util.ClientHelper;
import dev.andante.mccic.hud.client.config.HudClientConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import org.intellij.lang.annotations.RegExp;

public class WardrobeHudRenderer {
    public static final WardrobeHudRenderer INSTANCE = new WardrobeHudRenderer();

    private final MinecraftClient client;

    protected WardrobeHudRenderer() {
        this.client = MinecraftClient.getInstance();
    }

    public void render(Screen screen, MatrixStack matrices, int mouseX, int mouseY, float tickDelta) {
        if (HudClientConfig.getConfig().playerPreviewInWardrobe()) {
            Window window = this.client.getWindow();
            int width = window.getScaledWidth();
            int height = window.getScaledHeight();
            int x = (width / 2) - 170;
            int y = (height / 2) + 40;
            ClientHelper.drawOpaqueBlack(x - 50, y - 110, x + 50, y + 10);
            InventoryScreen.drawEntity(matrices, x - 10, y, 50, -40, 0, this.client.player);
        }
    }

    @RegExp
    public static String createGuiWardrobeTextPattern() {
        StringBuilder str = new StringBuilder();
        for (Icon icon : Icon.values()) {
            if (icon.name().startsWith(Icon.GUI_WARDROBE.name())) {
                str.append(UnicodeIconsStore.INSTANCE.getCharacterFor(icon)).append("|");
            }
        }
        return str.substring(0, str.length() - 1);
    }
}

package dev.andante.mccic.api.client.toast;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.andante.mccic.api.mixin.client.access.SystemToastAccessor;
import dev.andante.mccic.api.mixin.client.access.SystemToastTypeAccessor;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

@Environment(EnvType.CLIENT)
public class AdaptableIconToast extends CustomToast {
    public AdaptableIconToast(Identifier texture, Text title, @Nullable Text... lines) {
        super(title, null, texture);
        this.setLines(lines);
    }

    public AdaptableIconToast(Identifier texture, String id) {
        this(texture, Text.translatable("toast.%s.title".formatted(id)), Text.translatable("toast.%s.description".formatted(id)));
    }

    protected void setLines(@Nullable Text... lines) {
        ((SystemToastAccessor) this).setLines(Arrays.stream(lines).filter(Objects::nonNull).map(Text::asOrderedText).toList());
    }

    @Override
    public Toast.Visibility draw(DrawContext context, ToastManager manager, long startTime) {
        SystemToastAccessor that = (SystemToastAccessor) this;

        if (that.isJustUpdated()) {
            that.setStartTime(startTime);
            that.setJustUpdated(false);
        }

        Identifier texture = this.getTexture();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int width = this.getWidth();
        List<OrderedText> lines = that.getLines();
        if (width == 160 && lines.size() <= 1) {
            context.drawTexture(texture, 0, 0, 0, 64, width, this.getHeight());
        } else {
            int height = this.getHeight();
            int l = Math.min(4, height - 28);
            this.drawPart(context, width, 0, 0, 28);

            for (int i = 28; i < height - l; i += 10) {
                this.drawPart(context, width, 16, i, Math.min(16, height - i - l));
            }

            this.drawPart(context, width, 32 - l, height - l, l);
        }

        TextRenderer textRenderer = manager.getClient().textRenderer;
        Text title = that.getTitle();
        int x = 5 + 16 + 5 + 2;
        if (lines.isEmpty()) {
            context.drawTextWithShadow(textRenderer, title, x, 12, 0xFFFFFF00);
        } else {
            context.drawTextWithShadow(textRenderer, title, x, 7, 0xFFFFFF00);

            for (int i = 0; i < lines.size(); i++) {
                context.drawTextWithShadow(textRenderer, lines.get(i), x, 18 + i * 12, 0xFFFFFFFF);
            }
        }

        return startTime - that.getStartTime() < ((SystemToastTypeAccessor) (Object) that.getType()).getDisplayDuration() ? Visibility.SHOW : Visibility.HIDE;
    }

    protected void drawPart(DrawContext context, int width, int textureV, int y, int height) {
        int i = textureV == 0 ? 22 : 5;
        int j = Math.min(60, width - i);
        Identifier texture = this.getTexture();
        context.drawTexture(texture, 0, y, 0, 64 + textureV, i, height);

        for (int k = i; k < width - j; k += 64) {
            context.drawTexture(texture, k, y, 32, 64 + textureV, Math.min(64, width - k - j), height);
        }

        context.drawTexture(texture, width - j, y, 160 - j, 64 + textureV, j, height);
    }

    @Override
    public int getWidth() {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        SystemToastAccessor that = (SystemToastAccessor) this;
        int titleWidth = textRenderer.getWidth(that.getTitle());
        int linesWidth = that.getLines().stream().mapToInt(textRenderer::getWidth).max().orElse(0);
        return 35 + Math.max(titleWidth, linesWidth);
    }

    @Override
    public int getHeight() {
        SystemToastAccessor that = (SystemToastAccessor) this;
        return 20 + Math.max(that.getLines().size(), 1) * 12;
    }
}

package dev.andante.mccic.hud.mixin.client;

import dev.andante.mccic.api.client.tracker.GameTracker;
import dev.andante.mccic.api.client.tracker.QueueTracker;
import dev.andante.mccic.api.client.util.ClientHelper;
import dev.andante.mccic.hud.client.config.HudClientConfig;
import dev.andante.mccic.hud.client.config.HudPosition;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.OptionalInt;

@Environment(EnvType.CLIENT)
@Mixin(BossBarHud.class)
public abstract class BossBarHudMixin extends DrawableHelper {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(MatrixStack matrices, CallbackInfo ci) {
        if (GameTracker.INSTANCE.isOnServer()) {
            HudClientConfig config = HudClientConfig.getConfig();
            switch (config.topHud()) {
                case DISABLED -> ci.cancel();
                case CUSTOM -> {
                    Window window = this.client.getWindow();
                    int scaledWidth = window.getScaledWidth();
                    int scaledHeight = window.getScaledHeight();

                    TextRenderer textRenderer = this.client.textRenderer;

                    int y = 12;

                    GameTracker gameTracker = GameTracker.INSTANCE;
                    OptionalInt maybeGameTime = gameTracker.getTime();
                    if (maybeGameTime.isPresent()) {
                        int time = maybeGameTime.getAsInt();
                        Text text = Text.literal("%02d:%02d".formatted(time / 60, time % 60)).setStyle(Style.EMPTY.withFont(new Identifier("mcc", "hud_offset_3")));
                        int textWidth = 22;

                        int x;
                        HudPosition timerPosition = config.timerPosition();
                        if (timerPosition == HudPosition.TOP) {
                            x = (scaledWidth / 2) - (textWidth / 2) + 1;
                        } else if (timerPosition == HudPosition.LEFT) {
                            x = 12;
                            y = (scaledHeight / 2) - (textRenderer.fontHeight / 2);
                        } else {
                            throw new NotImplementedException("Hud position not implemented for game timer");
                        }

                        int endY = y + textRenderer.fontHeight + 1;
                        ClientHelper.drawOpaqueBlack(x - 3, y - 2, x + textWidth + 2, endY);
                        textRenderer.draw(matrices, text, x, y - 3, 0xFFFFFFFF);
                        y = endY;
                    }

                    QueueTracker queueTracker = QueueTracker.INSTANCE;
                    OptionalInt maybeQueueTime = queueTracker.getTime();
                    if (maybeQueueTime.isPresent()) {
                        int time = maybeQueueTime.getAsInt();
                        Text text = Text.literal("" + time).setStyle(Style.EMPTY.withColor(Formatting.YELLOW).withFont(new Identifier("mcc", "hud_offset_3")));
                        int textWidth = textRenderer.getWidth(text);

                        int x;
                        HudPosition timerPosition = config.timerPosition();
                        if (timerPosition == HudPosition.TOP) {
                            x = (scaledWidth / 2) - (textWidth / 2) + 1;
                            y += 12;
                        } else if (timerPosition == HudPosition.LEFT) {
                            x = 12;
                            y += (scaledHeight / 2) - (textRenderer.fontHeight / 2);
                        } else {
                            throw new NotImplementedException("Hud position not implemented for queue timer");
                        }

                        int endY = y + textRenderer.fontHeight + 1;
                        ClientHelper.drawOpaqueBlack(x - 3, y - 2, x + textWidth + 2, endY);
                        textRenderer.draw(matrices, text, x, y - 3, 0xFFFFFFFF);
                        y = endY;
                    }

                    ci.cancel();
                }
            }
        }
    }
}

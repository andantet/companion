package dev.andante.mccic.hud.client.render;

import dev.andante.mccic.api.client.tracker.GameTracker;
import dev.andante.mccic.api.client.tracker.QueueTracker;
import dev.andante.mccic.api.client.tracker.QueueType;
import dev.andante.mccic.api.client.util.ClientHelper;
import dev.andante.mccic.api.game.Game;
import dev.andante.mccic.config.ConfigHolder;
import dev.andante.mccic.hud.MCCICHud;
import dev.andante.mccic.hud.client.config.HudClientConfig;
import dev.andante.mccic.hud.client.config.HudPosition;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.ToIntFunction;

@Environment(EnvType.CLIENT)
public class MCCIHudRenderer extends DrawableHelper {
    public static final MCCIHudRenderer INSTANCE = new MCCIHudRenderer();

    public static final int
        BORDER = 3,
        ELEMENT_SEPARATOR = BORDER * 2 + 2;

    public static final Identifier HUD_FONT = new Identifier("mcc", "hud");
    public static final Style HUD_FONT_STYLE = Style.EMPTY.withFont(HUD_FONT);

    public static final String
        HUD_TIME_TEXT = "text.%s.hud.time".formatted(MCCICHud.MOD_ID),
        HUD_TIME_IN_QUEUE_TEXT = "%s.in_queue".formatted(HUD_TIME_TEXT),
        HUD_TIME_PLAYER_COUNT_TEXT = "%s.player_count".formatted(HUD_TIME_TEXT),
        HUD_TIME_TELEPORTED_TEXT = "%s.teleported".formatted(HUD_TIME_TEXT);

    private final MinecraftClient client;

    private final List<Element>
        topElements = new ArrayList<>(),
        leftElements = new ArrayList<>();

    private int scaledWidth, scaledHeight;
    private TextRenderer textRenderer;

    protected MCCIHudRenderer() {
        this.client = MinecraftClient.getInstance();
    }

    public void refreshElementLists(ConfigHolder<HudClientConfig> configHolder) {
        this.topElements.clear();
        this.leftElements.clear();

        HudClientConfig config = configHolder.get();
        this.addElement(config.timerPosition(), new TimeElement());
        this.addElement(config.queuePosition(), new QueueElement());
    }

    public void addElement(HudPosition position, Element element) {
        if (position.isEnabled()) {
            (switch (position) {
                case TOP -> this.topElements;
                case LEFT -> this.leftElements;
                default -> throw new IllegalStateException();
            }).add(element);
        }
    }

    public void render(MatrixStack matrices, float tickDelta, HudClientConfig config) {
        Window window = this.client.getWindow();
        this.scaledWidth = window.getScaledWidth();
        this.scaledHeight = window.getScaledHeight();
        this.textRenderer = this.client.textRenderer;

        {
            int x = this.scaledWidth / 2;
            int y = ELEMENT_SEPARATOR;
            for (Element element : this.topElements) {
                if (element.shouldRender(config)) {
                    int width = element.getWidth();
                    int height = element.getHeight();
                    int ix = x - (element.getWidth() / 2);
                    ClientHelper.drawOpaqueBlack(ix - BORDER, y - BORDER, ix + width + BORDER, y + height + BORDER);
                    element.render(matrices, tickDelta, ix, y, config);
                    y += element.getHeight() + ELEMENT_SEPARATOR;
                }
            }
        }

        {
            int x = BORDER + 6;
            int y = (this.scaledHeight / 2) - (this.sum(
                this.leftElements.stream()
                                 .filter(element -> element.shouldRender(config)).toList(),
                Element::getHeight, ELEMENT_SEPARATOR
            ) / 2);
            for (Element element : this.leftElements) {
                if (element.shouldRender(config)) {
                    int width = element.getWidth();
                    int height = element.getHeight();
                    ClientHelper.drawOpaqueBlack(x - BORDER, y - BORDER, x + width + BORDER, y + height + BORDER);
                    element.render(matrices, tickDelta, x, y, config);
                    y += element.getHeight() + ELEMENT_SEPARATOR;
                }
            }
        }
    }

    public int sum(List<Element> elements, ToIntFunction<Element> function, int separator) {
        int size = elements.size();
        return size == 0 ? 0 : elements.stream().mapToInt(function).sum() + (separator * (size - 1));
    }

    public int max(List<Element> elements, ToIntFunction<Element> function) {
        return elements.stream().mapToInt(function).max().orElse(0);
    }

    @Environment(EnvType.CLIENT)
    public class TimeElement extends Element {
        @Override
        public boolean shouldRender(HudClientConfig config) {
            return GameTracker.INSTANCE.getTime().isPresent();
        }

        @Override
        public void render(MatrixStack matrices, float tickDelta, int x, int y, HudClientConfig config) {
            GameTracker gameTracker = GameTracker.INSTANCE;
            int time = gameTracker.getTime().orElse(0);
            Text text = Text.literal("%02d:%02d".formatted(time / 60, time % 60)).setStyle(HUD_FONT_STYLE);
            this.drawTextInfer(matrices, text, x, y - 1);
        }

        @Override
        public int getWidth() {
            return 22;
        }

        @Override
        public int getHeight() {
            return 6;
        }
    }

    @Environment(EnvType.CLIENT)
    public class QueueElement extends Element {
        @Override
        public boolean shouldRender(HudClientConfig config) {
            return QueueTracker.INSTANCE.getQueueType() != QueueType.NONE;
        }

        @Override
        public void render(MatrixStack matrices, float tickDelta, int x, int y, HudClientConfig config) {
            y -= 1;

            QueueTracker queueTracker = QueueTracker.INSTANCE;

            Optional<Game> maybeGame = queueTracker.getGame();
            if (maybeGame.isPresent()) {
                Game game = maybeGame.get();
                Text text = this.createGameText(game);
                this.drawTextInfer(matrices, text, x, y);
            }

            Text typeText = this.createQueueTypeText(queueTracker.getQueueType());
            this.drawTextInfer(matrices, typeText, x, y + 8);

            Text text = this.createTimeText(queueTracker.getTime(), queueTracker.getPlayers(), queueTracker.getMaxPlayers());
            this.drawTextInfer(matrices, text, x, y + 16);
        }

        public Text createGameText(Game game) {
            List<String> scoreboardNames = game.getScoreboardNames();
            return Text.literal(scoreboardNames.get(scoreboardNames.size() - 1)).setStyle(HUD_FONT_STYLE);
        }

        public Text createQueueTypeText(QueueType queueType) {
            return Text.literal(queueType.getName()).setStyle(HUD_FONT_STYLE.withColor(Formatting.GRAY));
        }

        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        public Text createTimeText(OptionalInt time, int players, int maxPlayers) {
            if (time.isEmpty()) {
                return Text.translatable(HUD_TIME_IN_QUEUE_TEXT);
            }

            return (time.getAsInt() != 0 ? Text.translatable(HUD_TIME_TEXT,
                Text.translatable(HUD_TIME_PLAYER_COUNT_TEXT, players, maxPlayers).formatted(Formatting.WHITE),
                Text.literal("" + time)
            ) : Text.translatable(HUD_TIME_TELEPORTED_TEXT)).setStyle(HUD_FONT_STYLE.withColor(Formatting.YELLOW));
        }

        @Override
        public boolean shouldCenterText() {
            return HudClientConfig.getConfig().queuePosition() == HudPosition.TOP;
        }

        @Override
        public int getWidth() {
            QueueTracker queueTracker = QueueTracker.INSTANCE;
            OptionalInt maybeTime = queueTracker.getTime();

            Text gameText = this.createGameText(queueTracker.getGame().orElseThrow());
            Text queueTypeText = this.createQueueTypeText(queueTracker.getQueueType());
            Text timeText = maybeTime.isPresent()
                ? this.createTimeText(maybeTime, queueTracker.getPlayers(), queueTracker.getMaxPlayers())
                : Text.empty();

            int largest = Math.max(Math.max(textRenderer.getWidth(gameText), textRenderer.getWidth(queueTypeText)), textRenderer.getWidth(timeText));
            return this.shouldCenterText() ? Math.max(largest, maybeTime.isPresent() ? 220 : 160) : largest;
        }

        @Override
        public int getHeight() {
            return 14 + (QueueTracker.INSTANCE.getTime().isPresent() ? 8 : 0);
        }
    }

    @Environment(EnvType.CLIENT)
    public abstract class Element {
        public abstract boolean shouldRender(HudClientConfig config);
        public abstract void render(MatrixStack matrices, float tickDelta, int x, int y, HudClientConfig config);

        public abstract int getWidth();
        public abstract int getHeight();

        public boolean shouldCenterText() {
            return true;
        }

        public void drawText(MatrixStack matrices, Text text, int x, int y, int color) {
            MCCIHudRenderer.this.textRenderer.draw(matrices, text, x, y, color);
        }

        public void drawText(MatrixStack matrices, Text text, int x, int y) {
            this.drawText(matrices, text, x, y, 0xFFFFFFFF);
        }

        public void drawCenteredText(MatrixStack matrices, Text text, int x, int y) {
            this.drawText(matrices, text, x + (this.getWidth() / 2) - (MCCIHudRenderer.this.textRenderer.getWidth(text) / 2), y, 0xFFFFFFFF);
        }

        public void drawTextInfer(MatrixStack matrices, Text text, int x, int y) {
            if (this.shouldCenterText()) {
                this.drawCenteredText(matrices, text, x, y);
            } else {
                this.drawText(matrices, text, x, y);
            }
        }
    }
}

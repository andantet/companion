package dev.andante.mccic.api.client.util;

import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.andante.mccic.api.client.UnicodeIconsStore;
import dev.andante.mccic.api.client.UnicodeIconsStore.Icon;
import dev.andante.mccic.api.mixin.client.access.BossBarHudAccessor;
import dev.andante.mccic.api.mixin.client.access.InGameHudAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public interface ClientHelper {
    static <T> T getFromClient(Function<MinecraftClient, T> function) {
        return function.apply(MinecraftClient.getInstance());
    }

    static <T> Optional<T> getFromClientPlayer(Function<ClientPlayerEntity, T> function) {
        ClientPlayerEntity player = getFromClient(client -> client.player);
        return player == null ? Optional.empty() : Optional.of(function.apply(player));
    }

    static <T> T getFromInGameHud(Function<InGameHud, T> function) {
        return function.apply(getFromClient(client -> client.inGameHud));
    }

    static <T> T getFromInGameHudAccessed(Function<InGameHudAccessor, T> function) {
        return getFromInGameHud(hud -> function.apply((InGameHudAccessor) hud));
    }

    static Map<UUID, ClientBossBar> getBossBars() {
        return getFromInGameHud(hud -> ((BossBarHudAccessor) hud.getBossBarHud()).getBossBars());
    }

    static Stream<ClientBossBar> getBossBarStream() {
        return getBossBars().values().stream();
    }

    static Optional<Text> getTitle() {
        return Optional.ofNullable(getFromInGameHudAccessed(InGameHudAccessor::getTitle));
    }

    static Optional<Text> getSubtitle() {
        return Optional.ofNullable(getFromInGameHudAccessed(InGameHudAccessor::getSubtitle));
    }

    static int getTitleFadeTicks() {
        return getFromInGameHudAccessed(InGameHudAccessor::getTitleFadeInTicks);
    }

    static boolean isFading() {
        return getTitle().map(Text::getContent)
                         .filter(LiteralTextContent.class::isInstance)
                         .map(LiteralTextContent.class::cast)
                         .map(LiteralTextContent::string)
                         .filter(s -> s.equals("" + UnicodeIconsStore.INSTANCE.getCharacterFor(Icon.FADE)))
                         .isPresent();
    }

    static Optional<Scoreboard> getScoreboard() {
        return getFromClientPlayer(PlayerEntity::getScoreboard);
    }

    static Scoreboard getScoreboardOrThrow() {
        return getScoreboard().orElseThrow(() -> new IllegalStateException("Scoreboard is not present when expected"));
    }

    static Optional<ScoreboardObjective> getSidebarObjective() {
        return getScoreboard().map(s -> s.getObjectiveForSlot(1));
    }

    static Optional<Text> getSidebarTitle() {
        return getSidebarObjective().map(ScoreboardObjective::getDisplayName);
    }

    static Optional<List<String>> getScoreboardPlayerNames() {
        return getSidebarObjective().map(objective -> {
            Scoreboard scoreboard = getScoreboardOrThrow();
            return scoreboard.getAllPlayerScores(objective)
                             .stream()
                             .sorted(ScoreboardPlayerScore.COMPARATOR.reversed())
                             .map(ScoreboardPlayerScore::getPlayerName)
                             .toList();
        });
    }

    @Nullable
    static Text getActionBarText() {
        return getFromInGameHudAccessed(InGameHudAccessor::getOverlayMessage);
    }

    static void drawOpaqueBlack(int x1, int y1, int x2, int y2) {
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.disableTexture();
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA, SrcFactor.ZERO, DstFactor.ONE);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        int alpha = (int) (255 * 0.5F);
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(x1, y1, 0).color(0, 0, 0, alpha).next();
        buffer.vertex(x1, y2, 0).color(0, 0, 0, alpha).next();
        buffer.vertex(x2, y2, 0).color(0, 0, 0, alpha).next();
        buffer.vertex(x2, y1, 0).color(0, 0, 0, alpha).next();
        tessellator.draw();
    }
}

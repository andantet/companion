package dev.andante.mccic.api.client.util;

import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.andante.mccic.api.mixin.client.access.BossBarHudAccessor;
import dev.andante.mccic.api.mixin.client.access.InGameHudAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public interface ClientHelper {
    static Map<UUID, ClientBossBar> getBossBars() {
        MinecraftClient client = MinecraftClient.getInstance();
        BossBarHud hud = client.inGameHud.getBossBarHud();
        return ((BossBarHudAccessor) hud).getBossBars();
    }

    static Stream<ClientBossBar> getBossBarStream() {
        return getBossBars().values().stream();
    }

    static Optional<Scoreboard> getScoreboard() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        return player == null ? Optional.empty() : Optional.ofNullable(player.getScoreboard());
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
        return ((InGameHudAccessor) MinecraftClient.getInstance().inGameHud).getOverlayMessage();
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

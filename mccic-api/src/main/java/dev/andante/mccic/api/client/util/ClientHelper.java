package dev.andante.mccic.api.client.util;

import dev.andante.mccic.api.mixin.client.access.BossBarHudAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;

import java.util.Map;
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
}

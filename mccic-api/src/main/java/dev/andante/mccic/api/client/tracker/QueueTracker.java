package dev.andante.mccic.api.client.tracker;

import dev.andante.mccic.api.client.util.ClientHelper;
import dev.andante.mccic.api.game.Game;
import dev.andante.mccic.api.game.GameRegistry;
import dev.andante.mccic.api.util.TextQuery;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.Text;
import org.intellij.lang.annotations.RegExp;

/**
 * Tracks active queue data.
 */
@Environment(EnvType.CLIENT)
public class QueueTracker {
    public static final QueueTracker INSTANCE = new QueueTracker();

    @RegExp
    public static final String
        PLAYER_COUNT_REGEX = "\\(([0-9]+)/([0-9]+)\\)",
        TIME_REGEX = " IN ([0-9]+)!";

    public static final Pattern
        PLAYER_COUNT_PATTERN = Pattern.compile(PLAYER_COUNT_REGEX),
        TIME_PATTERN = Pattern.compile(TIME_REGEX);

    private Game game;
    private QueueType queueType;
    private int time, players, maxPlayers;

    public QueueTracker() {
        this.queueType = QueueType.NONE;
        ClientTickEvents.END_WORLD_TICK.register(this::onWorldTick);
    }

    protected void onWorldTick(ClientWorld world) {
        ClientHelper.getBossBarStream()
                    .map(BossBar::getName)
                    .filter(text -> {
                        String raw = text.getString();
                        return raw.contains("IN QUEUE") || raw.contains("TELEPORTING") || raw.contains("TELEPORTED");
                    })
                    .findAny()
                    .ifPresentOrElse(this::ifPresent, this::reset);
    }

    protected void reset() {
        this.game = null;
        this.queueType = QueueType.NONE;
        this.time = -1;
        this.players = 0;
        this.maxPlayers = 0;
    }

    protected void ifPresent(Text text) {
        // is it quickplay?
        TextQuery.findText(text, "QUICKPLAY").ifPresentOrElse(query -> {
            this.game = null;
            this.queueType = QueueType.QUICKPLAY;
        }, () -> {
            // if not quickplay, find the queued game
            TextQuery.findText(text, "[A-Z ]+").ifPresentOrElse(query -> {
                Text result = query.getResult();
                String raw = result.getString();
                this.game = GameRegistry.INSTANCE.fromScoreboard(raw).getValue();
            }, () -> this.game = null);
        });

        // infer the queue type
        TextQuery.findText(text, "\\(").ifPresentOrElse(query -> {
            Text result = query.getOffsetResult(1);
            String raw = result.getString();
            this.queueType = QueueType.fromScoreboard(raw);
        }, () -> this.queueType = null);

        // infer the player count
        TextQuery.findText(text, PLAYER_COUNT_REGEX).ifPresentOrElse(query -> {
            Text result = query.getResult();
            String raw = result.getString();
            Matcher matcher = PLAYER_COUNT_PATTERN.matcher(raw);
            if (matcher.matches()) {
                int players = Integer.parseInt(matcher.group(1));
                int maxPlayers = Integer.parseInt(matcher.group(2));
                this.players = players;
                this.maxPlayers = maxPlayers;
            } else {
                this.players = 0;
                this.maxPlayers = 0;
            }
        }, () -> {
            this.players = 0;
            this.maxPlayers = 0;
        });

        // infer the countdown
        TextQuery.findText(text, TIME_REGEX).ifPresentOrElse(query -> {
            Text result = query.getResult();
            String raw = result.getString();
            Matcher matcher = TIME_PATTERN.matcher(raw);
            if (matcher.matches()) {
                this.time = Integer.parseInt(matcher.group(1));
            } else {
                this.time = -1;
            }
        }, () -> {
            Optional<TextQuery> query = TextQuery.findText(text, "TELEPORTED!");
            this.time = query.isPresent() ? 0 : -1;
        });
    }

    public Optional<Game> getGame() {
        return Optional.ofNullable(this.game);
    }

    public QueueType getQueueType() {
        return this.queueType;
    }

    public OptionalInt getTime() {
        return this.time == -1 ? OptionalInt.empty() : OptionalInt.of(this.time);
    }

    public int getPlayers() {
        return this.players;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public float getPlayerPercentage() {
        if (this.maxPlayers == 0) {
            return 0.0F;
        }

        return this.players / (float) this.maxPlayers;
    }

    /**
     * Whether the timer is ticking down for a game to begin.
     */
    public boolean isGameStarting() {
        return this.getTime().isPresent();
    }

    public boolean leaveQueue() {
        if (game == null) return false;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return false;
        player.networkHandler.sendCommand("leavequeue");
        return true;
    }
  
    public boolean isInQueue() {
        return this.queueType != QueueType.NONE;
    }
}

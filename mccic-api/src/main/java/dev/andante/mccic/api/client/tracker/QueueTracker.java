package dev.andante.mccic.api.client.tracker;

import dev.andante.mccic.api.client.util.ClientHelper;
import dev.andante.mccic.api.game.Game;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.Text;
import org.intellij.lang.annotations.RegExp;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tracks active queue data.
 */
@Environment(EnvType.CLIENT)
public class QueueTracker {
    public static final QueueTracker INSTANCE = new QueueTracker();

    @RegExp
    public static final String
        ALL_REGEX = ".+?(?=[A-Z])([A-Z ]+).{4}([A-Z ]+).{5}(.+).",
        TELEPORTING_REGEX = ".+\\(([0-9]+)/([0-9]+)\\).+?(?=[0-9])([0-9]+).+";

    public static final Pattern
        ALL_PATTERN = Pattern.compile(ALL_REGEX),
        TELEPORTING_PATTERN = Pattern.compile(TELEPORTING_REGEX);

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
                    .map(Text::getString)
                    .filter(s -> s.matches(ALL_REGEX))
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

    protected void ifPresent(String string) {
        Matcher matcher = ALL_PATTERN.matcher(string);
        if (matcher.matches()) {
            String gameId = matcher.group(1);
            if (string.contains("QUICKPLAY")) {
                this.game = null;
                this.queueType = QueueType.QUICKPLAY;
            } else {
                this.game = Game.fromScoreboard(gameId).getValue();

                String queueTypeId = matcher.group(2);
                QueueType queueType = QueueType.fromScoreboard(queueTypeId);
                this.queueType = queueType == null ? QueueType.NONE : queueType;
            }

            if (string.contains("TELEPORTED!")) {
                this.time = 0;
            } else {
                String timeLine = matcher.group(3);
                Matcher timeMatcher = TELEPORTING_PATTERN.matcher(timeLine);
                if (timeMatcher.matches()) {
                    this.players = Integer.parseInt(timeMatcher.group(1));
                    this.maxPlayers = Integer.parseInt(timeMatcher.group(2));
                    this.time = timeMatcher.matches() ? Integer.parseInt(timeMatcher.group(3)) : -1;
                } else {
                    this.time = -1;
                }
            }
        } else {
            this.reset();
        }
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
}

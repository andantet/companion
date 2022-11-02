package dev.andante.mccic.discordrp.client;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.entities.User;
import com.jagrosh.discordipc.entities.pipe.PipeStatus;
import com.mojang.logging.LogUtils;
import dev.andante.mccic.api.MCCIC;
import dev.andante.mccic.api.client.tracker.GameTracker;
import dev.andante.mccic.api.client.tracker.QueueTracker;
import dev.andante.mccic.api.client.tracker.QueueType;
import dev.andante.mccic.api.game.Game;
import dev.andante.mccic.api.game.GameState;
import dev.andante.mccic.discordrp.MCCICDiscordRP;
import dev.andante.mccic.discordrp.client.config.DiscordRPClientConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import org.slf4j.Logger;

import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Environment(EnvType.CLIENT)
public class DiscordRichPresence {
    public static final String QUEUE_TEXT = "text.%s.queue".formatted(MCCICDiscordRP.MOD_ID);
    public static final String QUEUE_QUICKPLAY_TEXT = "%s.quickplay".formatted(QUEUE_TEXT);
    public static final String IDLE_TEXT = "text.%s.idle".formatted(MCCICDiscordRP.MOD_ID);
    public static final String GAME_TEXT = "text.%s.game_display".formatted(MCCICDiscordRP.MOD_ID);
    public static final String LARGE_IMAGE_TEXT = "text.%s.large_image_text".formatted(MCCICDiscordRP.MOD_ID);

    private static final Logger LOGGER = LogUtils.getLogger();

    private final long clientId;
    private final ScheduledExecutorService executorService;

    private OffsetDateTime initialTime;
    private IPCClient discord;

    public DiscordRichPresence(long clientId) {
        this.clientId = clientId;
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        this.initialTime = OffsetDateTime.now();
    }

    public void tryConnect() {
        if (this.discord != null) {
            return;
        }

        LOGGER.info("{}: Setting up Discord client", MCCIC.MOD_NAME);
        this.executorService.scheduleAtFixedRate(this::update, 0, 1, TimeUnit.SECONDS);

        this.discord = new IPCClient(this.clientId);
        this.discord.setListener(new IPCListener() {
            @Override
            public void onReady(IPCClient client, User user) {
                LOGGER.info("{}: Discord client ready", MCCIC.MOD_NAME);
            }
        });

        try {
            this.discord.connect();
        } catch (Exception exception) {
            LOGGER.error("Unable to connect to the Discord client", exception);
        }
    }

    public void tryDisconnect() {
        if (!this.executorService.isShutdown()) {
            this.executorService.shutdown();
        }

        if (this.discord != null) {
            LOGGER.info("{}: Closing Discord client", MCCIC.MOD_NAME);
            try {
                this.discord.close();
            } catch (Exception ignored) {
            }
        }
    }

    protected void update() {
        if (this.discord == null || this.discord.getStatus() != PipeStatus.CONNECTED) {
            return;
        }

        RichPresence.Builder builder = new RichPresence.Builder().setLargeImage("logo-mcci", I18n.translate(LARGE_IMAGE_TEXT))
                                                                 .setState(I18n.translate(IDLE_TEXT))
                                                                 .setStartTimestamp(this.initialTime);

        DiscordRPClientConfig config = DiscordRPClientConfig.getConfig();
        boolean displayGame = config.displayGame();

        QueueTracker queueTracker = QueueTracker.INSTANCE;
        QueueType queueType = queueTracker.getQueueType();
        if (queueType != QueueType.NONE && config.displayQueue()) {
            builder.setDetails(I18n.translate(QUEUE_TEXT));

            Optional<Game> maybeGame = queueTracker.getGame();
            if (maybeGame.isPresent()) {
                Game game = maybeGame.get();
                builder.setState(displayGame ? I18n.translate(GAME_TEXT, game.getDisplayName(), queueType.getDisplayName()) : queueType.getDisplayName());
                builder.setSmallImage(displayGame ? getIconForGame(game) : null);
            } else {
                builder.setState(I18n.translate(QUEUE_QUICKPLAY_TEXT));
                builder.setSmallImage("shuffle");
            }

            builder.setStartTimestamp(null);

            if (config.displayGameTime()) {
                setEndTimestampIfPresent(queueTracker.getTime().orElse(-1), builder);
            }
        } else {
            GameTracker gameTracker = GameTracker.INSTANCE;
            Optional<Game> maybeGame = gameTracker.getGame();
            if (maybeGame.isPresent() && config.displayGame()) {
                Game game = maybeGame.get();
                String displayName = game.getDisplayName();
                builder.setDetails(displayName);
                GameState state = gameTracker.getGameState();
                builder.setState(config.displayGameState() ? I18n.translate("text.%s.state.%s".formatted(MCCICDiscordRP.MOD_ID, state.name().toLowerCase(Locale.ROOT))) : null);
                builder.setSmallImage(getIconForGame(game), displayName);
                setEndTimestampIfPresent(gameTracker.getTime().orElse(-1), builder);
            }
        }

        this.discord.sendRichPresence(builder.build());
    }

    public static void setEndTimestampIfPresent(int time, RichPresence.Builder builder) {
        if (time != -1) {
            builder.setStartTimestamp(null);
            builder.setEndTimestamp(OffsetDateTime.now().plusSeconds(time));
        }
    }

    public static String getIconForGame(Game game) {
        return "logo_game-%s".formatted(game.getId());
    }

    public void resetInitialTime() {
        this.initialTime = OffsetDateTime.now();
    }
}

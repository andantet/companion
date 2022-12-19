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
import dev.andante.mccic.api.game.GameRegistry;
import dev.andante.mccic.api.game.GameState;
import dev.andante.mccic.discordrp.MCCICDiscordRP;
import dev.andante.mccic.discordrp.client.config.DiscordRPClientConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import org.slf4j.Logger;

import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.OptionalInt;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Environment(EnvType.CLIENT)
public class DiscordRichPresence {
    public static final String QUEUE_TEXT = "text.%s.queue".formatted(MCCICDiscordRP.MOD_ID);
    public static final String QUEUE_PLAYERS_TEXT = "%s.players".formatted(QUEUE_TEXT);
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
        this.resetInitialTime();
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

        DiscordRPClientConfig config = DiscordRPClientConfig.getConfig();
        RichPresenceBuilder builder = new RichPresenceBuilder(config);

        QueueTracker queueTracker;
        if (config.displayQueue() && (queueTracker = QueueTracker.INSTANCE).isInQueue()) {
            builder.setupQueue(queueTracker);
        } else {
            GameTracker gameTracker;
            if (config.displayGame() && (gameTracker = GameTracker.INSTANCE).isInGame()) {
                builder.setupGame(gameTracker, gameTracker.getGame().orElseThrow(() -> new IllegalStateException("Conditional conflicts with expected state")));
            }
        }

        this.discord.sendRichPresence(builder.build());
    }

    public class RichPresenceBuilder {
        private final RichPresence.Builder builder;
        private final DiscordRPClientConfig config;

        public RichPresenceBuilder(DiscordRPClientConfig config) {
            this.builder = new RichPresence.Builder().setLargeImage("logo-mcci", I18n.translate(LARGE_IMAGE_TEXT))
                                                     .setState(I18n.translate(IDLE_TEXT))
                                                     .setStartTimestamp(DiscordRichPresence.this.initialTime);
            this.config = config;
        }


        public void setupQueue(QueueTracker tracker) {
            boolean displayGame = this.config.displayGame();
            int max = tracker.getMaxPlayers();
            if (max != 0 && displayGame) {
                this.builder.setDetails(I18n.translate(QUEUE_PLAYERS_TEXT, tracker.getPlayers(), tracker.getMaxPlayers()));
            } else {
                this.builder.setDetails(I18n.translate(QUEUE_TEXT));
            }

            tracker.getGame().ifPresentOrElse(
                    game -> {
                        QueueType type = tracker.getQueueType();
                        String typeName = type.getDisplayName();
                        if (displayGame) {
                            String gameName = game.getDisplayString();
                            this.builder.setState(I18n.translate(GAME_TEXT, gameName, typeName));
                            this.setImagesForGame(game, gameName);
                        } else {
                            this.builder.setState(typeName);
                            this.builder.setSmallImage(null);
                        }
                    },
                    () -> {
                        this.builder.setState(I18n.translate(QUEUE_QUICKPLAY_TEXT));
                        this.builder.setSmallImage("shuffle");
                    }
            );

            this.builder.setStartTimestamp(null);
            this.setEndTimestampIfPresent(tracker.getTime());
        }

        public void setupGame(GameTracker tracker, Game game) {
            String name = game.getDisplayString();
            this.setImagesForGame(game, name);
            builder.setDetails(name);

            if (this.config.displayGameState()) {
                GameState state = tracker.getGameState();
                builder.setState(I18n.translate("text.%s.state.%s".formatted(MCCICDiscordRP.MOD_ID, state.name().toLowerCase(Locale.ROOT))));
            } else {
                builder.setState(null);
            }

            if (this.config.displayGameTime()) {
                this.setEndTimestampIfPresent(tracker.getTime());
            }
        }

        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        public void setEndTimestampIfPresent(OptionalInt time) {
            time.ifPresent(value -> {
                this.builder.setStartTimestamp(null);
                this.builder.setEndTimestamp(OffsetDateTime.now().plusSeconds(value));
            });
        }

        public void setImagesForGame(Game game, String text) {
            String id = GameRegistry.INSTANCE.getId(game);
            if (this.config.displayGameArt()) {
                this.builder.setSmallImage(null);
                this.builder.setLargeImage("art-%s".formatted(id), text);
            } else {
                this.builder.setSmallImage("logo_game-%s".formatted(id), text);
            }
        }

        public RichPresence build() {
            return this.builder.build();
        }
    }

    public void resetInitialTime() {
        this.initialTime = OffsetDateTime.now();
    }
}

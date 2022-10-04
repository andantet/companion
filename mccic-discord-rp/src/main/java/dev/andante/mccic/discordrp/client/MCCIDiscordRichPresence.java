package dev.andante.mccic.discordrp.client;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.entities.User;
import com.jagrosh.discordipc.entities.pipe.PipeStatus;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;
import com.mojang.logging.LogUtils;
import dev.andante.mccic.api.MCCIC;
import dev.andante.mccic.api.client.game.GameTracker;
import dev.andante.mccic.api.game.Game;
import dev.andante.mccic.api.game.GameState;
import org.slf4j.Logger;

import java.time.OffsetDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MCCIDiscordRichPresence {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ScheduledExecutorService executorService;
    private final long clientId;

    private IPCClient discord;

    public MCCIDiscordRichPresence(long clientId) {
        this.clientId = clientId;
        this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void tryConnect() {
        // Connect to the client in the thread to prevent blocking logic
        if (this.discord != null) {
            return;
        }

        LOGGER.info("{}: Setting up Discord client", MCCIC.MOD_NAME);
        this.executorService.scheduleAtFixedRate(this::update, 0, 5, TimeUnit.SECONDS);

        this.discord = new IPCClient(this.clientId);
        this.discord.setListener(new IPCListener() {
            @Override
            public void onReady(IPCClient client, User user) {
                LOGGER.info("{}: Discord client ready", MCCIC.MOD_NAME);
            }
        });

        try {
            this.discord.connect();
        } catch (NoDiscordClientException exception) {
            LOGGER.error("Unable to connect to the Discord client", exception);
        }
    }

    public void tryDisconnect() {
        if (!this.executorService.isShutdown()) {
            this.executorService.shutdown();
        }

        if (this.discord != null) {
            LOGGER.info("{}: Closing Discord client", MCCIC.MOD_NAME);
            this.discord.close();
        }
    }

    protected void update() {
        if (this.discord == null || this.discord.getStatus() != PipeStatus.CONNECTED) {
            return;
        }

        RichPresence.Builder builder = new RichPresence.Builder().setLargeImage("logo", "MCCI: Companion");

        GameTracker tracker = GameTracker.INSTANCE;
        Game game = tracker.getGame();
        if (game != null) {
            String displayName = game.getDisplayName();
            builder.setDetails(displayName);
            GameState state = tracker.getGameState();
            builder.setState(switch (state) {
                case ACTIVE -> "Playing";
                case WAITING_FOR_GAME -> "Waiting to begin...";
                case POST_ROUND_SELF -> "Waiting for other players...";
                case POST_ROUND -> "Waiting for the next round...";
                case POST_GAME -> "Finished!";
                default -> "Waiting...";
            });
            builder.setSmallImage("logo_game-%s".formatted(game.getId()), displayName);
            tracker.getTime().ifPresent(time -> builder.setEndTimestamp(OffsetDateTime.now().plusSeconds(time)));
        } else {
            builder.setState("Chilling on the island");
        }

        this.discord.sendRichPresence(builder.build());
    }
}

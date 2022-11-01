package dev.andante.mccic.discordrp.client;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.entities.User;
import com.jagrosh.discordipc.entities.pipe.PipeStatus;
import com.mojang.logging.LogUtils;
import dev.andante.mccic.api.MCCIC;
import dev.andante.mccic.api.client.tracker.GameTracker;
import dev.andante.mccic.api.game.Game;
import dev.andante.mccic.api.game.GameState;
import dev.andante.mccic.discordrp.MCCICDiscordRP;
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
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final OffsetDateTime INITIAL_TIME = OffsetDateTime.now();
    private final ScheduledExecutorService executorService;
    private final long clientId;

    private IPCClient discord;

    public DiscordRichPresence(long clientId) {
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

        RichPresence.Builder builder = new RichPresence.Builder().setLargeImage("logo-mcci", "MCCI: Companion");

        GameTracker tracker = GameTracker.INSTANCE;
        Optional<Game> maybeGame = tracker.getGame();
        if (maybeGame.isPresent()) {
            Game game = maybeGame.get();
            String displayName = game.getDisplayName();
            builder.setDetails(displayName);
            GameState state = tracker.getGameState();
            builder.setState(I18n.translate("text.%s.state.%s".formatted(MCCICDiscordRP.MOD_ID, state.name().toLowerCase(Locale.ROOT))));
            builder.setSmallImage("logo_game-%s".formatted(game.getId()), displayName);
            tracker.getTime().ifPresent(time -> builder.setEndTimestamp(OffsetDateTime.now().plusSeconds(time)));
        } else {
            builder.setState(I18n.translate("text.%s.idle".formatted(MCCICDiscordRP.MOD_ID))).setStartTimestamp(INITIAL_TIME);
        }

        this.discord.sendRichPresence(builder.build());
    }
}

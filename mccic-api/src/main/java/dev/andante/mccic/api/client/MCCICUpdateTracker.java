package dev.andante.mccic.api.client;

import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.andante.mccic.api.MCCIC;
import dev.andante.mccic.api.client.game.GameTracker;
import dev.andante.mccic.api.client.toast.MCCICToast;
import dev.andante.mccic.api.util.JsonHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.Text;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class MCCICUpdateTracker {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final MCCICUpdateTracker INSTANCE = new MCCICUpdateTracker("https://gist.githubusercontent.com/andantet/9da23e1fa711912449e9342da238b248/raw/mccic.json");

    public static final String UPDATE_POPUP_TITLE = "text.%s-api.update_available.title".formatted(MCCIC.MOD_ID);
    public static final String UPDATE_POPUP_DESCRIPTION = "text.%s-api.update_available.description".formatted(MCCIC.MOD_ID);

    private final URL url;
    private Data data;

    public MCCICUpdateTracker(String url) {
        try {
            this.url = new URL(url);
        } catch (MalformedURLException exception) {
            throw new RuntimeException(exception);
        }

        ClientPlayConnectionEvents.JOIN.register(this::onServerJoin);
    }

    protected void onServerJoin(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client) {
        if (GameTracker.INSTANCE.isOnServer()) {
            MCCICUpdateTracker updateTracker = MCCICUpdateTracker.INSTANCE;
            updateTracker.retrieve();
            Optional<MCCICUpdateTracker.Data> maybeData = updateTracker.getData();
            if (maybeData.isPresent() && updateTracker.isUpdateAvailable()) {
                MCCICUpdateTracker.Data data = maybeData.get();
                ToastManager toastManager = client.getToastManager();
                toastManager.add(new MCCICToast(Text.translatable(UPDATE_POPUP_TITLE, data.latest()), Text.translatable(UPDATE_POPUP_DESCRIPTION)));
            }
        }
    }

    /**
     * Updates {@link #data} based on the data given from the defined {@link #url}.
     */
    public void retrieve() {
        try {
            JsonReader reader = new JsonReader(new InputStreamReader(url.openStream()));
            Optional<Data> maybeData = Data.CODEC.parse(JsonOps.INSTANCE, JsonHelper.parseJsonReader(reader)).result();
            this.data = maybeData.orElseThrow(() -> new IOException("Codec could not parse data from update tracker source file"));
        } catch (IOException | JsonSyntaxException | IllegalStateException exception) {
            LOGGER.error("%s: Retrieval Error".formatted(MCCIC.MOD_NAME), exception);
        }
    }

    public URL getUrl() {
        return this.url;
    }

    public Optional<Data> getData() {
        return Optional.ofNullable(this.data);
    }

    public boolean isUpdateAvailable() {
        if (this.data != null) {
            Optional<ModContainer> maybeMod = FabricLoader.getInstance().getModContainer(MCCIC.MOD_ID);
            if (maybeMod.isPresent()) {
                Optional<SemanticVersion> maybeUpdate = this.data.createSemanticVersion();
                if (maybeUpdate.isPresent()) {
                    Version version = maybeMod.get().getMetadata().getVersion();
                    SemanticVersion updateVersion = maybeUpdate.get();
                    return version instanceof SemanticVersion && updateVersion.compareTo(version) > 0;
                }
            }
        }

        return false;
    }

    public record Data(int schemaVersion, String latest) {
        public static final Codec<Data> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                Codec.INT.fieldOf("schema_version").forGetter(Data::schemaVersion),
                Codec.STRING.fieldOf("latest").forGetter(Data::latest)
            ).apply(instance, Data::new)
        );

        public Optional<SemanticVersion> createSemanticVersion() {
            try {
                return Optional.of(SemanticVersion.parse(latest));
            } catch (VersionParsingException exception) {
                LOGGER.error("Semantic version parse exception", exception);
                return Optional.empty();
            }
        }
    }
}
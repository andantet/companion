package dev.andante.mccic.api.mccapi;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.andante.mccic.api.MCCIC;
import dev.andante.mccic.api.util.JsonHelper;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

public class EventApiHook {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final EventApiHook INSTANCE = new EventApiHook("https://api.mcchampionship.com/v1/event");

    private final URL url;
    private Data data;

    public EventApiHook(String url) {
        try {
            this.url = new URL(url);
        } catch (MalformedURLException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Updates {@link #data} based on the data given from the defined {@link #url}.
     */
    public void retrieve() {
        try {
            JsonReader reader = new JsonReader(new InputStreamReader(url.openStream()));
            JsonObject root = JsonHelper.parseJsonReader(reader).getAsJsonObject();
            Optional<Data> maybeData = Data.CODEC.parse(JsonOps.INSTANCE, root.getAsJsonObject("data")).result();
            this.data = maybeData.orElseThrow(() -> new IOException("Codec could not parse data from Event API"));
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

    public boolean isEventDateInFuture() {
        if (this.data != null) {
            Optional<Date> maybeDate = this.data.createDate();
            if (maybeDate.isPresent()) {
                return Calendar.getInstance().getTime().before(maybeDate.get());
            }
        }

        return false;
    }

    public record Data(String date, String event, String updateVideo) {
        public static final Codec<Data> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                Codec.STRING.fieldOf("date")
                    .xmap(Function.identity(), s -> s.substring(0, s.length() - 5) + "Z")
                    .forGetter(Data::date),
                Codec.STRING.fieldOf("event")
                    .forGetter(Data::event),
                Codec.STRING.fieldOf("updateVideo")
                    .orElse("")
                    .forGetter(Data::updateVideo)
            ).apply(instance, Data::new)
        );

        public Optional<Date> createDate() {
            try {
                TemporalAccessor temporalAccessor = DateTimeFormatter.ISO_INSTANT.parse(this.date);
                Instant instant = Instant.from(temporalAccessor);
                return Optional.of(Date.from(instant));
            } catch (DateTimeParseException exception) {
                LOGGER.error("Date parse exception", exception);
                return Optional.empty();
            }
        }
    }
}

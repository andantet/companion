package dev.andante.mccic.api.mccapi;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.andante.mccic.api.util.JsonHelper;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import org.slf4j.Logger;

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
    public CompletableFuture<EventApiHook> retrieve() {
        return CompletableFuture.supplyAsync(() -> {
            JsonHelper.parseCodecUrl(this.url, Data.CODEC, data -> this.data = data);
            return this;
        });
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

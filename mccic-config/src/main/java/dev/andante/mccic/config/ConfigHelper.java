package dev.andante.mccic.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonReader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import dev.andante.mccic.api.MCCIC;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public interface ConfigHelper {
    Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve(MCCIC.MOD_ID);

    MutableText RELOAD_TITLE_TEXT = Text.translatable("text.%s.config_reload_title".formatted(MCCIC.MOD_ID));
    MutableText RELOAD_DESCRIPTION_TEXT = Text.translatable("text.%s.config_reload_description".formatted(MCCIC.MOD_ID));

    static File resolveConfigFile(String module) {
        return CONFIG_PATH.resolve("%s-%s.json".formatted(MCCIC.MOD_ID, module)).toFile();
    }

    static <T extends Record> Optional<T> load(String module, Codec<T> codec, T defaultConfig) {
        File file = resolveConfigFile(module);
        if (file.exists()) {
            try {
                JsonReader reader = new JsonReader(new FileReader(file));
                Optional<T> result = codec.parse(JsonOps.INSTANCE, JsonHelper.parseJsonReader(reader)).result();
                reader.close();
                if (result.isPresent()) {
                    return result;
                } else {
                    MCCIC.LOGGER.error("Failed to parse: {}", module);
                }
            } catch (IOException exception) {
                MCCIC.LOGGER.error("Failed to load: {}", module);
                exception.printStackTrace();
            }
        } else {
            save(module, codec, defaultConfig);
        }

        return Optional.empty();
    }

    static <T extends Record> void save(String module, Codec<T> codec, T config) {
        Optional<JsonElement> result = codec.encodeStart(JsonOps.INSTANCE, config).result();
        if (result.isPresent()) {
            try {
                Gson gson = new GsonBuilder().create();
                File file = resolveConfigFile(module);
                File parent = file.getParentFile();
                if (!parent.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    parent.mkdirs();
                }
                FileWriter writer = new FileWriter(file);
                gson.toJson(result.get(), writer);
                writer.close();
            } catch (IOException exception) {
                MCCIC.LOGGER.error("Failed to load: {}", module);
                exception.printStackTrace();
            }
        } else {
            MCCIC.LOGGER.error("Failed to encode: {}", module);
        }
    }
}

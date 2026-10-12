package org.afv.arqueoutils.world;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.util.math.BlockPos;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class DeathRegistry {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Path FILE;

    public static void setWorld(Path worldDir) {
        FILE = worldDir.resolve("death_log.json");
    }

    private static final Map<Long, DeathEntry> DEATHS = new HashMap<>();

    public static void load() {
        DEATHS.clear();

        if (!Files.exists(FILE)) return;

        try {
            JsonObject root = JsonParser.parseString(Files.readString(FILE)).getAsJsonObject();

            for (String timeString : root.keySet()) {
                JsonObject obj = root.getAsJsonObject(timeString);
                BlockPos pos;
                String name;
                String dimension;

                String posString = Codec.STRING
                        .parse(JsonOps.INSTANCE, obj.get("location"))
                        .getOrThrow();

                String[] parts = posString.split(", ");

                pos = new BlockPos(
                        Integer.parseInt(parts[0]),
                        Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2])
                );

                dimension = Codec.STRING
                        .parse(JsonOps.INSTANCE, obj.get("dimension"))
                        .getOrThrow();
                name = Codec.STRING
                        .parse(JsonOps.INSTANCE, obj.get("name"))
                        .getOrThrow();

                DEATHS.put(Long.parseLong(timeString), new DeathEntry(name, pos, dimension, Long.parseLong(timeString)));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load death_log", e);
        }
    }

    public static void save() {
        JsonObject root = new JsonObject();

        for (DeathEntry entry : DEATHS.values()) {
            JsonObject obj = new JsonObject();
            obj.add(
                    "name",
                    Codec.STRING.encodeStart(JsonOps.INSTANCE, entry.getPlayerName())
                            .getOrThrow()
            );
            obj.add(
                    "location",
                    Codec.STRING.encodeStart(JsonOps.INSTANCE, entry.getLocString())
                            .getOrThrow()
            );
            obj.add(
                    "dimension",
                    Codec.STRING.encodeStart(JsonOps.INSTANCE, entry.getDimension())
                            .getOrThrow()
            );
            obj.add(
                    "timestamp",
                    Codec.STRING.encodeStart(JsonOps.INSTANCE, entry.getTimestampUTC())
                            .getOrThrow()
            );
            root.add("" + entry.getTime(), obj);
        }

        try {
            Files.writeString(FILE, GSON.toJson(root));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void register(DeathEntry death) {
        DEATHS.put(death.getTime(), death);
        save();
    }

    public static List<Long> getDeathTimeList() {
        return new ArrayList<>(DEATHS.keySet());
    }

    public static void remove(long id) {
        DEATHS.remove(id);
        save();
    }

    public static DeathEntry get(long id) {
        return DEATHS.get(id);
    }

    public static Collection<DeathEntry> values() {
        return DEATHS.values();
    }
}

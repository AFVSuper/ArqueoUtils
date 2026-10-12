package org.afv.arqueoutils.util;

import com.google.gson.*;
import net.minecraft.entity.player.PlayerEntity;
import org.afv.arqueoutils.ArqueoUtils;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ServerCapeStorage {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<UUID, CustomCape> CAPES = new HashMap<>();
    private static Path PATH;

    public ServerCapeStorage() {}

    public static void setPath(Path path) {
        ServerCapeStorage.PATH = path;
    }

    public static void load() {
        CAPES.clear();
        Path path = PATH;

        if (!Files.exists(path)) {
            return; // no file yet is valid
        }

        try (Reader reader = Files.newBufferedReader(path)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

            for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
                UUID uuid = UUID.fromString(entry.getKey());
                String capeId = entry.getValue().getAsString();

                CustomCape cape = CustomCape.getById(capeId);
                if (cape != null) {
                    CAPES.put(uuid, cape);
                }
            }
        } catch (Exception e) {
            ArqueoUtils.LOGGER.error("Failed to load capes", e);
        }
    }

    public static void save() {
        Path path = PATH;
        JsonObject root = new JsonObject();

        for (var entry : CAPES.entrySet()) {
            CustomCape cape = entry.getValue();
            if (cape == null) root.addProperty(entry.getKey().toString(), "");
            else root.addProperty(entry.getKey().toString(), cape.getId());
        }

        try (Writer writer = Files.newBufferedWriter(path)) {
            GSON.toJson(root, writer);
        } catch (IOException e) {
            ArqueoUtils.LOGGER.error("Failed to save capes", e);
        }
    }

    public static Map<UUID, CustomCape> getAll() {
        return CAPES;
    }

    public static void setCape(PlayerEntity player, CustomCape cape) {
        CAPES.put(player.getUuid(), cape);
        save();
    }

    @Nullable
    public static CustomCape getCape(UUID uuid) {
        return CAPES.get(uuid);
    }
}
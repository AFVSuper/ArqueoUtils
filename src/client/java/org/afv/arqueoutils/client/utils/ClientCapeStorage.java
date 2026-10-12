package org.afv.arqueoutils.client.utils;

import org.afv.arqueoutils.util.CustomCape;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientCapeStorage {
    private static final Map<UUID, CustomCape> CAPES = new HashMap<>();

    public static void setCape(UUID uuid, CustomCape cape) {
        CAPES.put(uuid, cape);
    }

    public static CustomCape getCape(UUID uuid) {
        return CAPES.get(uuid);
    }

    public static void clearCape(UUID uuid) {
        CAPES.remove(uuid);
    }
}

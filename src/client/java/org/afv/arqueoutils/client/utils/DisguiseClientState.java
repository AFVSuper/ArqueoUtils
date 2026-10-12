package org.afv.arqueoutils.client.utils;

import net.minecraft.client.render.entity.state.EntityRenderState;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

public final class DisguiseClientState {
    private static final Set<UUID> DISGUISED = ConcurrentHashMap.newKeySet();
    private static final Map<EntityRenderState, UUID> STATE_UUIDS =
            java.util.Collections.synchronizedMap(new WeakHashMap<>());

    private DisguiseClientState() {}

    public static void set(UUID uuid, boolean disguised) {
        if (disguised) {
            DISGUISED.add(uuid);
        } else {
            DISGUISED.remove(uuid);
        }
    }

    public static Set<UUID> getHiddenPlayers() {
        return Set.copyOf(DISGUISED);
    }

    public static boolean isDisguised(UUID uuid) {
        return DISGUISED.contains(uuid);
    }

    public static void bind(EntityRenderState state, UUID uuid) {
        STATE_UUIDS.put(state, uuid);
    }

    public static boolean isDisguised(EntityRenderState state) {
        UUID uuid = STATE_UUIDS.get(state);
        return uuid != null && isDisguised(uuid);
    }
}

package org.afv.arqueoutils.util;

import net.minecraft.server.world.ServerWorld;

public class GlobalState {
    public static boolean isBWEnabled(ServerWorld world) {
        GlobalBWState state = world.getPersistentStateManager()
                .getOrCreate(GlobalBWState.GLOBAL_BW_TYPE);
        return state.isEnabled();
    }

    public static void setBWEnabled(ServerWorld world, boolean bl) {
        GlobalBWState state = world.getPersistentStateManager()
                .getOrCreate(GlobalBWState.GLOBAL_BW_TYPE);
        state.setEnabled(bl);
    }
}

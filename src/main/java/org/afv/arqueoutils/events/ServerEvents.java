package org.afv.arqueoutils.events;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.util.WorldSavePath;
import org.afv.arqueoutils.world.DeathRegistry;

import java.nio.file.Path;

public class ServerEvents {
    public static void register() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            Path worldDir = server.getSavePath(WorldSavePath.ROOT);

            DeathRegistry.setWorld(worldDir);

            DeathRegistry.load();
        });
    }
}

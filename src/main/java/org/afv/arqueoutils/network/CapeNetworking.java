package org.afv.arqueoutils.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.afv.arqueoutils.util.CustomCape;
import org.afv.arqueoutils.util.ServerCapeStorage;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class CapeNetworking {
    public static void broadcastCape(UUID uuid, @Nullable CustomCape cape, MinecraftServer server) {
        CapeSyncPayload payload = new CapeSyncPayload(
                uuid,
                cape != null ? cape.getId() : ""
        );

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    public static void sendAllTo(ServerPlayerEntity target) {
        for (var entry : ServerCapeStorage.getAll().entrySet()) {
            CustomCape cape = entry.getValue();
            String id;
            if (cape == null) id = "";
            else id = cape.getId();
            CapeSyncPayload payload = new CapeSyncPayload(
                    entry.getKey(),
                    id
            );
            ServerPlayNetworking.send(target, payload);
        }
    }
}

package org.afv.arqueoutils.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;

import java.util.UUID;

public record DisguiseSyncPayload(UUID playerUuid, boolean disguised) implements CustomPayload {
    public static final Id<DisguiseSyncPayload> ID =
            new Id<>(Identifier.of(ArqueoUtils.ModID, "disguise_sync"));

    public static final PacketCodec<PacketByteBuf, DisguiseSyncPayload> CODEC =
            PacketCodec.of((payload, buf) -> {
                buf.writeUuid(payload.playerUuid());
                buf.writeBoolean(payload.disguised());
            }, buf -> new DisguiseSyncPayload(buf.readUuid(), buf.readBoolean()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

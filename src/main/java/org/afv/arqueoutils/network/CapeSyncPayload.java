package org.afv.arqueoutils.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;

import java.util.UUID;

public record CapeSyncPayload(UUID playerUuid, String capeId) implements CustomPayload {

    public static final Id<CapeSyncPayload> ID =
            new Id<>(Identifier.of(ArqueoUtils.ModID, "cape_sync"));

    public static final PacketCodec<PacketByteBuf, CapeSyncPayload> CODEC =
            PacketCodec.of((payload, buf) -> {
                buf.writeUuid(payload.playerUuid());
                buf.writeString(payload.capeId());
            }, buf -> new CapeSyncPayload(buf.readUuid(), buf.readString()));

    public CapeSyncPayload(PacketByteBuf buf) {
        this(buf.readUuid(), buf.readString());
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
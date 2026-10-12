package org.afv.arqueoutils.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;

import java.util.UUID;

public record ApplyScrollPayload(UUID playerUuid, String type, int quality) implements CustomPayload {

    public static final Id<ApplyScrollPayload> ID =
            new Id<>(Identifier.of(ArqueoUtils.ModID, "apply_scroll"));

    public static final PacketCodec<PacketByteBuf, ApplyScrollPayload> CODEC =
            PacketCodec.of((payload, buf) -> {
                buf.writeUuid(payload.playerUuid());
                buf.writeString(payload.type());
                buf.writeInt(payload.quality());
            }, buf -> new ApplyScrollPayload(buf.readUuid(), buf.readString(), buf.readInt()));

    public ApplyScrollPayload(PacketByteBuf buf) {
        this(buf.readUuid(), buf.readString(), buf.readInt());
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
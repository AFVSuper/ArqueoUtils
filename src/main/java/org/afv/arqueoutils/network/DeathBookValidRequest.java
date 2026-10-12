package org.afv.arqueoutils.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;

public record DeathBookValidRequest(String senderName, String name) implements CustomPayload {
    public static final Id<DeathBookValidRequest> ID =
            new Id<>(Identifier.of(ArqueoUtils.ModID, "death_book_request"));

    public static final PacketCodec<PacketByteBuf, DeathBookValidRequest> CODEC =
            PacketCodec.of((payload, buf) -> {
                buf.writeString(payload.senderName());
                buf.writeString(payload.name());
            }, buf -> new DeathBookValidRequest(buf.readString(), buf.readString()));

    public DeathBookValidRequest(PacketByteBuf buf) {
        this(buf.readString(), buf.readString());
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

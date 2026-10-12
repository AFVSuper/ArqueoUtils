package org.afv.arqueoutils.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;

public record DeathBookValidResponse(boolean valid) implements CustomPayload {
    public static final Id<DeathBookValidResponse> ID =
            new Id<>(Identifier.of(ArqueoUtils.ModID, "death_book_response"));

    public static final PacketCodec<PacketByteBuf, DeathBookValidResponse> CODEC =
            PacketCodec.of((payload, buf) -> {
                buf.writeBoolean(payload.valid());
            }, buf -> new DeathBookValidResponse(buf.readBoolean()));

    public DeathBookValidResponse(PacketByteBuf buf) {
        this(buf.readBoolean());
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

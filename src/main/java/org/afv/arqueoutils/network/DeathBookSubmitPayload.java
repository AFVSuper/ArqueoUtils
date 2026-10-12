package org.afv.arqueoutils.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;

public record DeathBookSubmitPayload(String sender, String name) implements CustomPayload {
    public static final Id<DeathBookSubmitPayload> ID =
            new Id<>(Identifier.of(ArqueoUtils.ModID, "death_book_submit"));

    public static final PacketCodec<PacketByteBuf, DeathBookSubmitPayload> CODEC =
            PacketCodec.of((payload, buf) -> {
                buf.writeString(payload.sender());
                buf.writeString(payload.name());
            }, buf -> new DeathBookSubmitPayload(buf.readString(), buf.readString()));

    public DeathBookSubmitPayload(PacketByteBuf buf) {
        this(buf.readString(), buf.readString());
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

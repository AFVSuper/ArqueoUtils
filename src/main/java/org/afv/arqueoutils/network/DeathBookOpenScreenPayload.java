package org.afv.arqueoutils.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;

public record DeathBookOpenScreenPayload() implements CustomPayload {
    public static final Id<DeathBookOpenScreenPayload> ID =
            new Id<>(Identifier.of(ArqueoUtils.ModID, "death_book_open"));

    public static final PacketCodec<PacketByteBuf, DeathBookOpenScreenPayload> CODEC =
            PacketCodec.of((payload, buf) -> {
            }, buf -> new DeathBookOpenScreenPayload());

    public DeathBookOpenScreenPayload(PacketByteBuf buf) {
        this();
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

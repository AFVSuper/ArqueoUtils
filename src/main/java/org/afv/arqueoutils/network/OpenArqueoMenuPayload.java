package org.afv.arqueoutils.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;

public record OpenArqueoMenuPayload() implements CustomPayload {

    public static final Id<OpenArqueoMenuPayload> ID =
            new Id<>(Identifier.of(ArqueoUtils.ModID, "arqueo_menu"));

    public static final PacketCodec<PacketByteBuf, OpenArqueoMenuPayload> CODEC =
            PacketCodec.of((payload, buf) -> {
            }, buf -> new OpenArqueoMenuPayload());

    public OpenArqueoMenuPayload(PacketByteBuf buf) {
        this();
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
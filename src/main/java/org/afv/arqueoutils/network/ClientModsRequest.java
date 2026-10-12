package org.afv.arqueoutils.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;

public record ClientModsRequest() implements CustomPayload {

    public static final Id<ClientModsRequest> ID =
            new Id<>(Identifier.of(ArqueoUtils.ModID, "mods_request"));

    public static final PacketCodec<PacketByteBuf, ClientModsRequest> CODEC =
            PacketCodec.of((payload, buf) -> {
            }, buf -> new ClientModsRequest());

    public ClientModsRequest(PacketByteBuf buf) {
        this();
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
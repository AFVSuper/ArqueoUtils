package org.afv.arqueoutils.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;

public record ResourcesRequest() implements CustomPayload {

    public static final Id<ResourcesRequest> ID =
            new Id<>(Identifier.of(ArqueoUtils.ModID, "resources_request"));

    public static final PacketCodec<PacketByteBuf, ResourcesRequest> CODEC =
            PacketCodec.of((payload, buf) -> {
            }, buf -> new ResourcesRequest());

    public ResourcesRequest(PacketByteBuf buf) {
        this();
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
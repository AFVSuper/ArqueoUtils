package org.afv.arqueoutils.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;
import org.afv.arqueoutils.util.ResourceInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record ResourcesResponse(UUID playerUuid, List<ResourceInfo> resources) implements CustomPayload {

    public static final Id<ResourcesResponse> ID =
            new Id<>(Identifier.of(ArqueoUtils.ModID, "resources_response"));

    public static final PacketCodec<PacketByteBuf, ResourcesResponse> CODEC =
            PacketCodec.of((payload, buf) -> {
                writePayload(buf, payload);
            }, ResourcesResponse::readPayload);

    public ResourcesResponse(PacketByteBuf buf) {
        this(buf.readUuid(), readResourceList(buf));
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    /* ----------------- helper methods ----------------- */

    private static void writePayload(PacketByteBuf buf, ResourcesResponse payload) {
        buf.writeUuid(payload.playerUuid());
        buf.writeInt(payload.resources().size());
        for (ResourceInfo resource : payload.resources()) {
            writeResourceInfo(buf, resource);
        }
    }

    private static ResourcesResponse readPayload(PacketByteBuf buf) {
        UUID playerUuid = buf.readUuid();
        List<ResourceInfo> resources = readResourceList(buf);
        return new ResourcesResponse(playerUuid, resources);
    }

    private static void writeResourceInfo(PacketByteBuf buf, ResourceInfo rp) {
        buf.writeString(rp.name());
        buf.writeBoolean(rp.enabled());
    }

    private static ResourceInfo readResourceInfo(PacketByteBuf buf) {
        String name = buf.readString();
        boolean enabled = buf.readBoolean();
        return new ResourceInfo(name, enabled);
    }

    private static List<ResourceInfo> readResourceList(PacketByteBuf buf) {
        int size = buf.readInt();
        List<ResourceInfo> resources = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            resources.add(readResourceInfo(buf));
        }
        return resources;
    }
}
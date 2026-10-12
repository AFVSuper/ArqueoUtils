package org.afv.arqueoutils.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;
import org.afv.arqueoutils.util.ModInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record ClientModsResponse(UUID playerUuid, List<ModInfo> mods) implements CustomPayload {

    public static final Id<ClientModsResponse> ID =
            new Id<>(Identifier.of(ArqueoUtils.ModID, "mods_response"));

    public static final PacketCodec<PacketByteBuf, ClientModsResponse> CODEC =
            PacketCodec.of((payload, buf) -> {
                writePayload(buf, payload);
            }, ClientModsResponse::readPayload);

    public ClientModsResponse(PacketByteBuf buf) {
        this(buf.readUuid(), readModList(buf));
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    /* ----------------- helper methods ----------------- */

    private static void writePayload(PacketByteBuf buf, ClientModsResponse payload) {
        buf.writeUuid(payload.playerUuid());
        buf.writeInt(payload.mods().size());
        for (ModInfo mod : payload.mods()) {
            writeModInfo(buf, mod);
        }
    }

    private static ClientModsResponse readPayload(PacketByteBuf buf) {
        UUID playerUuid = buf.readUuid();
        List<ModInfo> mods = readModList(buf);
        return new ClientModsResponse(playerUuid, mods);
    }

    private static void writeModInfo(PacketByteBuf buf, ModInfo mod) {
        buf.writeString(mod.name());
        buf.writeString(mod.version());
        buf.writeInt(mod.children().size());
        for (ModInfo child : mod.children()) {
            writeModInfo(buf, child);
        }
    }

    private static ModInfo readModInfo(PacketByteBuf buf) {
        String name = buf.readString();
        String version = buf.readString();
        int childCount = buf.readInt();
        List<ModInfo> children = new ArrayList<>();
        for (int i = 0; i < childCount; i++) {
            children.add(readModInfo(buf));
        }
        return new ModInfo(name, version, children);
    }

    private static List<ModInfo> readModList(PacketByteBuf buf) {
        int size = buf.readInt();
        List<ModInfo> mods = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            mods.add(readModInfo(buf));
        }
        return mods;
    }
}
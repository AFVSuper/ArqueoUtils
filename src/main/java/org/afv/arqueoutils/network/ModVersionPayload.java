package org.afv.arqueoutils.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;

public record ModVersionPayload(String version) implements CustomPayload {
    public static final Id<ModVersionPayload> ID =
            new Id<>(Identifier.of(ArqueoUtils.ModID, "mod_version"));

    public static final PacketCodec<PacketByteBuf, ModVersionPayload> CODEC = PacketCodec.of(
            (value, buf) -> buf.writeString(value.version()),
            buf -> new ModVersionPayload(buf.readString())
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
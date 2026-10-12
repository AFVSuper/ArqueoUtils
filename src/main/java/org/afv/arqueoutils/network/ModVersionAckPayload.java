package org.afv.arqueoutils.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;

public record ModVersionAckPayload(boolean matches) implements CustomPayload {
    public static final Id<ModVersionAckPayload> ID =
            new Id<>(Identifier.of(ArqueoUtils.ModID, "mod_version_ack"));

    public static final PacketCodec<PacketByteBuf, ModVersionAckPayload> CODEC = PacketCodec.of(
            (value, buf) -> buf.writeBoolean(value.matches()),
            buf -> new ModVersionAckPayload(buf.readBoolean())
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
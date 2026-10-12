package org.afv.arqueoutils.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;

public record GlobalBWPayload(boolean enabled) implements CustomPayload {

    public static final Id<GlobalBWPayload> ID =
            new Id<>(Identifier.of(ArqueoUtils.ModID, "global_bw"));

    public static final PacketCodec<PacketByteBuf, GlobalBWPayload> CODEC =
            PacketCodec.of(
                (payload, buf) -> buf.writeBoolean(payload.enabled),
                buf -> new GlobalBWPayload(buf.readBoolean())
            );

    public GlobalBWPayload(PacketByteBuf buf) {
        this(buf.readBoolean());
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
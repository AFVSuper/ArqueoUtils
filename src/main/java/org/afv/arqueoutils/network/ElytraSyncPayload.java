package org.afv.arqueoutils.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;

public record ElytraSyncPayload(boolean elytraAdv) implements CustomPayload {

    public static final Id<ElytraSyncPayload> ID =
            new Id<>(Identifier.of(ArqueoUtils.ModID, "elytra_sync"));

    public static final PacketCodec<PacketByteBuf, ElytraSyncPayload> CODEC =
            PacketCodec.of((payload, buf) -> {
                buf.writeBoolean(payload.elytraAdv());
            }, buf -> new ElytraSyncPayload(buf.readBoolean()));

    public ElytraSyncPayload(PacketByteBuf buf) {
        this(buf.readBoolean());
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
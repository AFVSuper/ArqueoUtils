package org.afv.arqueoutils.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;

public record DarkSpearLungePayload() implements CustomPayload {

    public static final Id<DarkSpearLungePayload> ID =
            new Id<>(Identifier.of(ArqueoUtils.ModID, "dark_spear_lunge"));

    public static final PacketCodec<PacketByteBuf, DarkSpearLungePayload> CODEC =
            PacketCodec.of((payload, buf) -> {
            }, buf -> new DarkSpearLungePayload());

    public DarkSpearLungePayload(PacketByteBuf buf) {
        this();
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
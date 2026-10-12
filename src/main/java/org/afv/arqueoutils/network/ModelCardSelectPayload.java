package org.afv.arqueoutils.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;

public record ModelCardSelectPayload(Hand cardHand, String model) implements CustomPayload {
    /** Empty model string means reset to the vanilla appearance. */
    public static final Id<ModelCardSelectPayload> ID =
            new Id<>(Identifier.of(ArqueoUtils.ModID, "model_card_select"));

    public static final PacketCodec<PacketByteBuf, ModelCardSelectPayload> CODEC =
            PacketCodec.of((payload, buf) -> {
                buf.writeEnumConstant(payload.cardHand());
                buf.writeString(payload.model());
            }, buf -> new ModelCardSelectPayload(buf.readEnumConstant(Hand.class), buf.readString()));

    public ModelCardSelectPayload(PacketByteBuf buf) {
        this(buf.readEnumConstant(Hand.class), buf.readString());
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

package org.afv.arqueoutils.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;

import java.util.ArrayList;
import java.util.List;

public record ModelCardOpenScreenPayload(Hand cardHand, List<String> models) implements CustomPayload {
    public static final Id<ModelCardOpenScreenPayload> ID =
            new Id<>(Identifier.of(ArqueoUtils.ModID, "model_card_open"));

    public static final PacketCodec<PacketByteBuf, ModelCardOpenScreenPayload> CODEC =
            PacketCodec.of((payload, buf) -> {
                buf.writeEnumConstant(payload.cardHand());
                buf.writeCollection(payload.models(), PacketByteBuf::writeString);
            }, buf -> new ModelCardOpenScreenPayload(
                    buf.readEnumConstant(Hand.class),
                    buf.readCollection(ArrayList::new, PacketByteBuf::readString)
            ));

    public ModelCardOpenScreenPayload(PacketByteBuf buf) {
        this(buf.readEnumConstant(Hand.class), buf.readCollection(ArrayList::new, PacketByteBuf::readString));
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

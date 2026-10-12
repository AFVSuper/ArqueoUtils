package org.afv.arqueoutils.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;

public record ArqueoWorldStateC2SPayload(String id, Object value) implements CustomPayload {

    public static final Id<ArqueoWorldStateC2SPayload> ID =
            new Id<>(Identifier.of(ArqueoUtils.ModID, "arqueo_state_c2s"));

    public static final PacketCodec<PacketByteBuf, ArqueoWorldStateC2SPayload> CODEC =
            PacketCodec.of(
                (payload, buf) -> {
                        buf.writeString(payload.id);
                        Object v = payload.value;

                        switch (v) {
                            case Integer i -> {
                                buf.writeByte(0);
                                buf.writeInt(i);
                            }
                            case Boolean b -> {
                                buf.writeByte(1);
                                buf.writeBoolean(b);
                            }
                            case String s -> {
                                buf.writeByte(2);
                                buf.writeString(s);
                            }
                            default -> throw new IllegalArgumentException("Unsupported payload type: " + v.getClass());
                        }
                    },
                    buf -> {
                        String id = buf.readString();
                        int type = buf.readByte();

                        Object value = switch (type) {
                            case 0 -> buf.readInt();
                            case 1 -> buf.readBoolean();
                            case 2 -> buf.readString();
                            default -> throw new IllegalStateException("Unknown type: " + type);
                        };

                        return new ArqueoWorldStateC2SPayload(id, value);
                    }
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
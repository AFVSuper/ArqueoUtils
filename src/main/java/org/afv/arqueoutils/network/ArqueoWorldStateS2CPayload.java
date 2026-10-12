package org.afv.arqueoutils.network;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;
import org.afv.arqueoutils.world.ArqueoWorldState;

public record ArqueoWorldStateS2CPayload(NbtCompound data) implements CustomPayload {

    public static final Id<ArqueoWorldStateS2CPayload> ID =
            new Id<>(Identifier.of(ArqueoUtils.ModID, "world_state_s2c"));

    public static final PacketCodec<RegistryByteBuf, ArqueoWorldStateS2CPayload> CODEC =
            PacketCodec.of(
                    ArqueoWorldStateS2CPayload::write,
                    ArqueoWorldStateS2CPayload::read
            );

    // Constructor builds NBT from the state automatically
    public ArqueoWorldStateS2CPayload(MinecraftServer server) {
        this(buildNbt(ArqueoWorldState.get(server)));
    }

    private static NbtCompound buildNbt(ArqueoWorldState state) {
        NbtCompound tag = new NbtCompound();

        for (String key : ArqueoWorldState.DATA_TYPES.keySet()) {
            Class<?> type = ArqueoWorldState.DATA_TYPES.get(key);

            if (type == Integer.class) {
                tag.putInt(key, state.getValue(key, 0));
            } else if (type == Boolean.class) {
                tag.putBoolean(key, state.getValue(key, false));
            }
            // add other types here if needed
        }

        return tag;
    }

    private static ArqueoWorldStateS2CPayload read(RegistryByteBuf buf) {
        return new ArqueoWorldStateS2CPayload(buf.readNbt());
    }

    private void write(RegistryByteBuf buf) {
        buf.writeNbt(data);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
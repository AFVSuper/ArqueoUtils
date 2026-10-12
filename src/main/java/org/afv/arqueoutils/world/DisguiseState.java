package org.afv.arqueoutils.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DisguiseState extends PersistentState {
    private static final String KEY = "arqueoutils_disguise";

    public static final Codec<DisguiseState> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.listOf().fieldOf("disguised").forGetter(DisguiseState::encoded)
            ).apply(instance, DisguiseState::fromEncoded)
    );

    public static final PersistentStateType<DisguiseState> TYPE = new PersistentStateType<>(
            KEY,
            DisguiseState::new,
            CODEC,
            DataFixTypes.LEVEL
    );

    private final Set<UUID> disguised = new HashSet<>();

    public DisguiseState() {}

    private static DisguiseState fromEncoded(java.util.List<String> encoded) {
        DisguiseState state = new DisguiseState();
        for (String entry : encoded) {
            try {
                state.disguised.add(UUID.fromString(entry));
            } catch (IllegalArgumentException ignored) {
            }
        }
        return state;
    }

    private java.util.List<String> encoded() {
        return disguised.stream().map(UUID::toString).toList();
    }

    public static DisguiseState get(MinecraftServer server) {
        return server.getOverworld().getPersistentStateManager().getOrCreate(TYPE);
    }

    public boolean isDisguised(UUID uuid) {
        return disguised.contains(uuid);
    }

    public void setDisguised(UUID uuid, boolean value) {
        if (value) {
            disguised.add(uuid);
        } else {
            disguised.remove(uuid);
        }
        markDirty();
    }

    public Set<UUID> all() {
        return Set.copyOf(disguised);
    }
}

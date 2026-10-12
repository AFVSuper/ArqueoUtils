package org.afv.arqueoutils.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

import java.util.Map;

public class ArqueoWorldState extends PersistentState {

    private static final String KEY = "arqueoutils_world";

    public static final Map<String, Class<?>> DATA_TYPES = Map.of(
            "completed_cores", Integer.class,
            "end_enabled", Boolean.class,
            "fast_xp_pickup", Boolean.class
    );

    public static final Codec<ArqueoWorldState> ARQUEO_WORLD_CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            Codec.INT.fieldOf("completed_cores").forGetter(ArqueoWorldState::getCompletedCores),
                            Codec.BOOL.fieldOf("end_enabled").forGetter(ArqueoWorldState::isEndEnabled),
                            Codec.BOOL.fieldOf("fast_xp_pickup").forGetter(ArqueoWorldState::isFastXpPickup)
                    ).apply(instance, ArqueoWorldState::new)
            );

    public static final PersistentStateType<ArqueoWorldState> ARQUEO_WORLD_TYPE =
            new PersistentStateType<>(
                    KEY, // ID used in saving
                    ArqueoWorldState::new,      // No-arg constructor
                    ARQUEO_WORLD_CODEC,          // Codec for serialization
                    DataFixTypes.LEVEL   // Typical type for world saved data
            );

    private int completedCores = 0;
    private boolean endEnabled = false;
    private boolean fastXpPickup = false;

    public ArqueoWorldState() {}

    public ArqueoWorldState(int completedCores, boolean endEnabled, boolean fastXpPickup) {
        this.completedCores = completedCores;
        this.endEnabled = endEnabled;
        this.fastXpPickup = fastXpPickup;
    }

    /* ---------- getters ---------- */

    @SuppressWarnings("unchecked")
    public <T> T getValue(String key, T def) {
        Object value;

        switch (key) {
            case "completed_cores" -> value = completedCores;
            case "end_enabled" -> value = endEnabled;
            case "fast_xp_pickup" -> value = fastXpPickup;
            default -> {
                return def;
            }
        }

        assert (!def.getClass().isInstance(value)) : "The class of the value " + key + " is not " + def.getClass().getName();

        return (T) value;
    }

    public int getCompletedCores() {
        return completedCores;
    }

    public boolean isEndEnabled() { return endEnabled; }

    public boolean isFastXpPickup() {
        return fastXpPickup;
    }

    /* ---------- modifiers ---------- */

    public void incrementCores() {
        completedCores++;
        markDirty();
    }
    public void setCompletedCores(int completedCores) {
        this.completedCores = completedCores;
        markDirty();
    }

    public void setEndEnabled(boolean bl) {
        this.endEnabled = bl;
        markDirty();
    }

    public void setFastXpPickup(boolean bl) {
        this.fastXpPickup = bl;
        markDirty();
    }


//    /* ---------- load ---------- */
//
//    public static ArqueoWorldState createFromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
//
//        ArqueoWorldState state = new ArqueoWorldState();
//
//        state.completedCores = nbt.getInt("completed_cores").orElse(0);
//
//        return state;
//    }

    public static ArqueoWorldState get(MinecraftServer server) {
        return server.getSpawnWorld().getPersistentStateManager()
                .getOrCreate(ARQUEO_WORLD_TYPE);
    }

    public void fromTag(NbtCompound tag) {
        this.completedCores = tag.getInt("completed_cores").orElse(0);
        this.endEnabled = tag.getBoolean("end_enabled").orElse(false);
        this.fastXpPickup = tag.getBoolean("fast_xp_pickup").orElse(false);
    }

    // Called when Minecraft saves the world
    public NbtCompound toTag(NbtCompound tag) {
        tag.putInt("completed_cores", completedCores);
        tag.putBoolean("end_enabled", endEnabled);
        tag.putBoolean("fast_xp_pickup", fastXpPickup);
        return tag;
    }
}
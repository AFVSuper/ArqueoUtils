package org.afv.arqueoutils.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

public class GlobalBWState extends PersistentState {
    public static final Codec<GlobalBWState> GLOBAL_BW_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.fieldOf("enabled").forGetter(GlobalBWState::isEnabled)
            ).apply(instance, (enabled) -> {
                GlobalBWState state = new GlobalBWState();
                state.setEnabled(enabled);
                return state;
            })
    );

    public static final PersistentStateType<GlobalBWState> GLOBAL_BW_TYPE =
            new PersistentStateType<>(
                    "arqueoutils_global_bw", // ID used in saving
                    GlobalBWState::new,      // No-arg constructor
                    GLOBAL_BW_CODEC,          // Codec for serialization
                    DataFixTypes.LEVEL   // Typical type for world saved data
            );

    private boolean enabled = false;

    // getter / setter
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        markDirty(); // mark state to save
    }

    // Called when Minecraft loads the world
    public void fromTag(NbtCompound tag) {
        this.enabled = tag.getBoolean("enabled").isPresent() && tag.getBoolean("enabled").get();
    }

    // Called when Minecraft saves the world
    public NbtCompound toTag(NbtCompound tag) {
        tag.putBoolean("enabled", enabled);
        return tag;
    }
}
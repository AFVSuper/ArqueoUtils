package org.afv.arqueoutils.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CTMState extends PersistentState {
    private static final String KEY = "ctm_state";

    public static enum CoreColor {
        WHITE("white"),
        ORANGE("orange"),
        MAGENTA("magenta"),
        LIGHT_BLUE("light_blue"),
        YELLOW("yellow"),
        LIME("lime"),
        PINK("pink"),
        GRAY("gray"),
        LIGHT_GRAY("light_gray"),
        CYAN("cyan"),
        PURPLE("purple"),
        BLUE("blue"),
        BROWN("brown"),
        GREEN("green"),
        RED("red"),
        BLACK("black");

        private final String name;

        CoreColor(String name) {
            this.name = name;
        }

        public static CoreColor getByName(String name) {
            for (CoreColor coreColor : CoreColor.values()) {
                if (coreColor.name.equalsIgnoreCase(name)) return coreColor;
            }
            return null;
        }

        public String getName() {
            return name;
        }

        public static final Codec<CoreColor> CODEC =
                Codec.STRING.xmap(
                        s -> Arrays.stream(values())
                                .filter(c -> c.name.equals(s))
                                .findFirst()
                                .orElseThrow(() -> new IllegalArgumentException("Unknown core color: " + s)),
                        CoreColor::getName
                );
    }

    private static final Codec<Map<CoreColor, String>> PLAYER_MAP_CODEC =
            Codec.unboundedMap(CoreColor.CODEC, Codec.STRING);

    public static final Codec<CTMState> CTM_STATE_CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            PLAYER_MAP_CODEC
                                    .fieldOf("players")
                                    .forGetter(CTMState::getPlayerMap),
                            Codec.BOOL
                                    .optionalFieldOf("wither_skeleton_obtained", false)
                                    .forGetter(CTMState::hasWitherSkeletonObtained),
                            Codec.INT
                                    .optionalFieldOf("ancient_city_chests", 0)
                                    .forGetter(CTMState::getAncientCityChestsOpened)
                    ).apply(instance, CTMState::new)
            );

    public static final PersistentStateType<CTMState> CTM_STATE_TYPE =
            new PersistentStateType<>(
                    KEY, // ID used in saving
                    CTMState::new,      // No-arg constructor
                    CTM_STATE_CODEC,          // Codec for serialization
                    DataFixTypes.LEVEL   // Typical type for world saved data
            );

    private final Map<CoreColor, String> playerMap = new HashMap<>();
    private boolean witherSkeletonObtained = false;
    private int ancientCityChestsOpened = 0;

    public CTMState() {}

    public CTMState(Map<CoreColor, String> playerMap, boolean witherSkeletonObtained, int ancientCityChestsOpened) {
        this.playerMap.putAll(playerMap);
        this.witherSkeletonObtained = witherSkeletonObtained;
        this.ancientCityChestsOpened = ancientCityChestsOpened;
    }

    /* ---------- getters ---------- */

    public Map<CoreColor, String> getPlayerMap() {
        return playerMap;
    }

    public boolean hasWitherSkeletonObtained() {
        return witherSkeletonObtained;
    }

    public int getAncientCityChestsOpened() {
        return ancientCityChestsOpened;
    }

    /* ---------- modifiers ---------- */

    public void setPlayerForCore(CoreColor core, PlayerEntity player) {
        playerMap.put(core, player.getNameForScoreboard());
        markDirty();
    }

    public void clearPlayerMap() {
        playerMap.clear();
        markDirty();
    }

    public void setWitherSkeletonObtained(boolean obtained) {
        if (this.witherSkeletonObtained != obtained) {
            this.witherSkeletonObtained = obtained;
            markDirty();
        }
    }

    public void markWitherSkeletonObtained() {
        if (!witherSkeletonObtained) {
            witherSkeletonObtained = true;
            markDirty();
        }
    }

    public void setAncientCityChestsOpened(int chests) {
        markDirty();
        ancientCityChestsOpened = chests;
    }

    public void incrementAncientCityChestsOpened() {
        markDirty();
        ++ancientCityChestsOpened;
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

    public static CTMState get(MinecraftServer server) {
        return server.getSpawnWorld().getPersistentStateManager()
                .getOrCreate(CTM_STATE_TYPE);
    }

//    public void fromTag(NbtCompound tag) {
//        this.playerMap.clear();
//        for (CoreColor core : CoreColor.values()) {
//            String player = String.valueOf(tag.getString(core.name()));
//            if (player == null) continue;
//            this.playerMap.put(core, player);
//        }
//    }
//
//    // Called when Minecraft saves the world
//    public NbtCompound toTag(NbtCompound tag) {
//        for (CoreColor core : CoreColor.values()) {
//            if (!playerMap.containsKey(core)) continue;
//            tag.putString(core.name(), playerMap.get(core));
//        }
//        return tag;
//    }
}

package org.afv.arqueoutils.enchantment;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import java.util.HashMap;
import java.util.Map;

public final class PulverizingOreDrop {
    private static final Map<Block, String> BLOCK_TO_POWDER_TYPE = new HashMap<>();

    static {
        registerOrePair(Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE, "coal");
        registerOrePair(Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE, "copper");
        registerOrePair(Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE, "iron");
        registerOrePair(Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE, "gold");
        registerOrePair(Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE, "diamond");
        registerOrePair(Blocks.EMERALD_ORE, Blocks.DEEPSLATE_EMERALD_ORE, "emerald");
        registerOrePair(Blocks.LAPIS_ORE, Blocks.DEEPSLATE_LAPIS_ORE, "lapis");
        registerOrePair(Blocks.REDSTONE_ORE, Blocks.DEEPSLATE_REDSTONE_ORE, "redstone");
        BLOCK_TO_POWDER_TYPE.put(Blocks.NETHER_QUARTZ_ORE, "quartz");
        BLOCK_TO_POWDER_TYPE.put(Blocks.NETHER_GOLD_ORE, "gold");
    }

    private static void registerOrePair(Block normal, Block deepslate, String powderType) {
        BLOCK_TO_POWDER_TYPE.put(normal, powderType);
        BLOCK_TO_POWDER_TYPE.put(deepslate, powderType);
    }

    private PulverizingOreDrop() {}

    /**
     * @return powder type key for {@link org.afv.arqueoutils.init.ModComponents#ORE_POWDER_TYPE}, or {@code null}
     */
    public static String getPowderType(Block block) {
        return BLOCK_TO_POWDER_TYPE.get(block);
    }
}

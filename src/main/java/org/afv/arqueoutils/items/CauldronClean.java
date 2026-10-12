package org.afv.arqueoutils.items;

import net.minecraft.block.BlockState;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.afv.arqueoutils.init.ModComponents;
import org.afv.arqueoutils.init.ModItems;

public class CauldronClean {
    public static void init() {
        CauldronBehavior.WATER_CAULDRON_BEHAVIOR.map().put(
                ModItems.LUMINIS_HELMET,
                CauldronClean::cleanArmor
        );
        CauldronBehavior.WATER_CAULDRON_BEHAVIOR.map().put(
                ModItems.LUMINIS_CHESTPLATE,
                CauldronClean::cleanArmor
        );
        CauldronBehavior.WATER_CAULDRON_BEHAVIOR.map().put(
                ModItems.LUMINIS_LEGGINGS,
                CauldronClean::cleanArmor
        );
        CauldronBehavior.WATER_CAULDRON_BEHAVIOR.map().put(
                ModItems.LUMINIS_BOOTS,
                CauldronClean::cleanArmor
        );
    }

    private static ActionResult cleanArmor(
            BlockState state,
            World world,
            BlockPos pos,
            PlayerEntity player,
            Hand hand,
            ItemStack stack
    ) {
        if (!stack.isIn(ItemTags.DYEABLE)) {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
        } else if (!stack.contains(DataComponentTypes.DYED_COLOR)) {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
        } else {
            if (!world.isClient()) {
                stack.remove(DataComponentTypes.DYED_COLOR);
                player.incrementStat(Stats.CLEAN_ARMOR);
                LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
            }
            return ActionResult.SUCCESS;
        }
    }

    private static ActionResult cleanInvis(
            BlockState state,
            World world,
            BlockPos pos,
            PlayerEntity player,
            Hand hand,
            ItemStack stack
    ) {
        if (!stack.isIn(ItemTags.TRIMMABLE_ARMOR)) {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
        } else if (!stack.contains(ModComponents.INVISIBLE_DYED)) {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
        } else {
            if (!world.isClient()) {
                stack.remove(ModComponents.INVISIBLE_DYED);
                player.incrementStat(Stats.CLEAN_ARMOR);
                LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
            }
            return ActionResult.SUCCESS;
        }
    }
}

package org.afv.arqueoutils.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.server.world.ServerWorld;

public class SmeltLootFunction implements LootFunction {

    @Override
    public ItemStack apply(ItemStack stack, LootContext context) {
        if (!(context.getWorld() instanceof ServerWorld world)) {
            return stack;
        }

        Entity entity = context.get(LootContextParameters.ATTACKING_ENTITY);
        if (entity == null) entity = context.get(LootContextParameters.THIS_ENTITY);
        // Example: get player / entity
        if (!(entity instanceof LivingEntity living) || !EquipmentHelper.shouldSmeltDrops(living)) {
            return stack;
        }

        return SmeltUtils.trySmelt(world, stack, context.get(LootContextParameters.ORIGIN));
    }

    @Override
    public LootFunctionType<?> getType() {
        return LootFunctionTypes.SET_COUNT; // dummy, not serialized
    }
}
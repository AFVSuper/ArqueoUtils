package org.afv.arqueoutils.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.afv.arqueoutils.enchantment.ModEnchantments;
import org.afv.arqueoutils.enchantment.PulverizingOreDrop;
import org.afv.arqueoutils.init.ModComponents;
import org.afv.arqueoutils.init.ModItems;
import org.afv.arqueoutils.util.EnchHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Block.class)
public class BlockMixin {

    @Inject(
            method = "getDroppedStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)Ljava/util/List;",
            at = @At("RETURN"),
            cancellable = true)
    private static void arqueoutils$pulverizingOrePowder(
            BlockState state,
            ServerWorld world,
            BlockPos pos,
            BlockEntity blockEntity,
            Entity entity,
            ItemStack tool,
            CallbackInfoReturnable<List<ItemStack>> cir
    ) {
        String powderType = PulverizingOreDrop.getPowderType(state.getBlock());
        if (powderType == null) {
            return;
        }
        if (EnchHelper.getLevel(tool, world, Enchantments.SILK_TOUCH) > 0) {
            return;
        }
        if (EnchHelper.getLevel(tool, world, ModEnchantments.PULVERIZING) <= 0) {
            return;
        }
        List<ItemStack> drops = cir.getReturnValue();
        ItemStack powder = new ItemStack(ModItems.ORE_POWDER);
        powder.set(ModComponents.ORE_POWDER_TYPE, powderType);
        drops.add(powder);
        cir.setReturnValue(drops);
    }
}

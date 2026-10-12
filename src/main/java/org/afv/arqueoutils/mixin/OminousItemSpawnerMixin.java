package org.afv.arqueoutils.mixin;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.OminousItemSpawnerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.afv.arqueoutils.potion.ModPotions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OminousItemSpawnerEntity.class)
public abstract class OminousItemSpawnerMixin {

    @Inject(
        method = "create",
        at = @At("HEAD"),
            cancellable = true)
    private static void injectCustomPotion(
            World world, ItemStack stack, CallbackInfoReturnable<OminousItemSpawnerEntity> cir
    ) {
        if (!world.isClient()) {
            if (stack.isOf(Items.LINGERING_POTION) || stack.isOf(Items.SPLASH_POTION)) {
                if (world.random.nextFloat() > 0.2f) return;
            } else if (stack.isOf(Items.FIRE_CHARGE)) {
                if (world.random.nextFloat() > 0.5f) return;
                stack = new ItemStack(Items.SPLASH_POTION);
            } else return;
            if (world.random.nextFloat() < 0.5f) {
                stack.set(
                        DataComponentTypes.POTION_CONTENTS,
                        new PotionContentsComponent(
                                ModPotions.HEALTH_DISRUPTION_POTION
                        )
                );
            } else {
                stack.set(
                        DataComponentTypes.POTION_CONTENTS,
                        new PotionContentsComponent(
                                ModPotions.OXIDATION_POTION
                        )
                );
            }

            cir.setReturnValue(OminousItemSpawnerEntity.create(world, stack));
        }
    }
}
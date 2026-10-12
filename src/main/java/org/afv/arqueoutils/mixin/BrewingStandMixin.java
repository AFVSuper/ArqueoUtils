package org.afv.arqueoutils.mixin;

import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.BrewingRecipeRegistry;
import org.afv.arqueoutils.init.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BrewingStandBlockEntity.class)
public class BrewingStandMixin {

    @Redirect(
            method = "craft",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/recipe/BrewingRecipeRegistry;craft(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;"
            )
    )
    private static ItemStack arqueoutils$customBrewingResult(
            BrewingRecipeRegistry instance, ItemStack ingredient, ItemStack input
    ) {
        // check input potion
        if (!input.isEmpty()
                && input.isOf(Items.POTION)
                && isInvisPot(input)
                && ingredient.isOf(Items.INK_SAC)) {

            return new ItemStack(ModItems.INVISIBLE_INK);
        }

        // fallback vanilla
        return instance.craft(ingredient, input);
    }

    @Unique
    private static boolean isInvisPot(ItemStack stack) {
        // ArqueoUtils.LOGGER.info("isInvis Check");
        PotionContentsComponent comp = stack.get(DataComponentTypes.POTION_CONTENTS);
        if (comp == null) return false;
        for (StatusEffectInstance effect : comp.getEffects()) {
            if (effect.getEffectType() == StatusEffects.INVISIBILITY) return true;
        }
        return false;
    }
}
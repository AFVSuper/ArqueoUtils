package org.afv.arqueoutils.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.registry.entry.RegistryEntry;
import org.afv.arqueoutils.util.EquipmentHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ApplyBonusLootFunction.class)
public class ApplyBonusCountMixin {
    @Shadow
    @Final
    private RegistryEntry<Enchantment> enchantment;

    @ModifyExpressionValue(method = "process", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/enchantment/EnchantmentHelper;getLevel(Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/item/ItemStack;)I"
    ))
    private int addFortuneLevel(int level, ItemStack stack, LootContext lootContext) {
        Entity entity = lootContext.get(LootContextParameters.THIS_ENTITY);

        if (this.enchantment.matchesKey(Enchantments.FORTUNE) && entity instanceof LivingEntity livingEntity) {
            level += EquipmentHelper.getEnchantmentLevelIncrease(Enchantments.FORTUNE, livingEntity);
        }

        return level;
    }
}

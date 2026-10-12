package org.afv.arqueoutils.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.Optional;

public class EnchHelper {
    public static int getLevel(ItemStack stack, ServerWorld world, RegistryKey<Enchantment> enchantment) {
        return world.getRegistryManager()
                .getOrThrow(RegistryKeys.ENCHANTMENT)
                .getEntry(enchantment.getValue())
                .map(entry -> EnchantmentHelper.getLevel(entry, stack))
                .orElse(0);
    }

    public static int getLevel(ItemStack stack, RegistryKey<Enchantment> enchantment){
        for (RegistryEntry<Enchantment> enchantments : stack.getEnchantments().getEnchantments()){
            if (enchantments.toString().contains(enchantment.getValue().toString())){
                return stack.getEnchantments().getLevel(enchantments);
            }
        }
        return 0;
    }

    public static RegistryEntry<Enchantment> getEntry(RegistryKey<Enchantment> enchantment, World world){
        Registry<Enchantment> registry = world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT); // example, depends on context

        Optional<RegistryEntry.Reference<Enchantment>> opt = registry
                .getEntry(enchantment.getValue());

        return opt.orElse(null);
    }
}

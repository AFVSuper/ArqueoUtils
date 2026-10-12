package org.afv.arqueoutils.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import org.afv.arqueoutils.init.ModItems;

import java.util.Map;
import java.util.Set;

public class EquipmentHelper {
    private static final Map<Item, Item> NETHERITE_MAP = Map.of(
            Items.DIAMOND_AXE, Items.NETHERITE_AXE,
            Items.DIAMOND_SWORD, Items.NETHERITE_SWORD,
            Items.DIAMOND_SPEAR, Items.NETHERITE_SPEAR,
            Items.DIAMOND_HELMET, Items.NETHERITE_HELMET,
            Items.DIAMOND_CHESTPLATE, Items.NETHERITE_CHESTPLATE,
            Items.DIAMOND_LEGGINGS, Items.NETHERITE_LEGGINGS,
            Items.DIAMOND_BOOTS, Items.NETHERITE_BOOTS
    );

    private static final Map<Item, Item> LUMINIS_MAP = Map.of(
            Items.DIAMOND_HELMET, ModItems.LUMINIS_HELMET,
            Items.DIAMOND_CHESTPLATE, ModItems.LUMINIS_CHESTPLATE,
            Items.DIAMOND_LEGGINGS, ModItems.LUMINIS_LEGGINGS,
            Items.DIAMOND_BOOTS, ModItems.LUMINIS_BOOTS
    );

    private static final Set<Item> DRAGON_KILL_ITEMS = Set.of(
            Items.DRAGON_HEAD, Items.ELYTRA, Items.SHULKER_SHELL, Items.CHORUS_FLOWER, Items.CHORUS_FRUIT
    );

    public static boolean needsFreeTheEnd(ItemStack stack) {
        return DRAGON_KILL_ITEMS.contains(stack.getItem());
    }

    public static int getEnchantmentLevelIncrease(RegistryKey<Enchantment> enchantment, LivingEntity entity) {
        if (enchantment.equals(Enchantments.FORTUNE)) {
            if (entity.getOffHandStack().isOf(ModItems.FORTUNE_AMULET)) return 1;
        } else if (enchantment.equals(Enchantments.LOOTING)) {
            if (entity.getOffHandStack().isOf(ModItems.LOOTING_AMULET)) return 1;
        }
        return 0;
    }

    public static boolean shouldSmeltDrops(LivingEntity entity) {
        return entity.getOffHandStack().isOf(ModItems.SMELT_AMULET);
    }

    public static boolean hasPotionAmulet(LivingEntity entity) {
        return entity.getOffHandStack().isOf(ModItems.POTION_AMULET);
    }

    public static boolean hasForgeAmulet(LivingEntity entity) {
        return entity.getOffHandStack().isOf(ModItems.FORGE_AMULET);
    }

    public static Item mapNetheriteUpgrade(ItemStack stack) {
        return NETHERITE_MAP.getOrDefault(stack.getItem(), Items.AIR);
    }

    public static Item mapLuminisUpgrade(ItemStack stack) {
        return LUMINIS_MAP.getOrDefault(stack.getItem(), Items.AIR);
    }
}

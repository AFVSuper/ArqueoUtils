package org.afv.arqueoutils.enchantment;

import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;
import org.afv.arqueoutils.enchantment.custom.MagnetPowerEnchantmentEffect;
import org.afv.arqueoutils.enchantment.custom.MagnetRadiusEnchantmentEffect;
import org.afv.arqueoutils.enchantment.custom.PulverizingEnchantmentEffect;
import org.afv.arqueoutils.init.ModItems;

public class ModEnchantments {
    public static final RegistryKey<Enchantment> MAGNET_POWER =
            RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(ArqueoUtils.ModID, "attraction"));

    public static final RegistryKey<Enchantment> MAGNET_RADIUS =
            RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(ArqueoUtils.ModID, "magnetic_field"));

    public static final RegistryKey<Enchantment> PULVERIZING =
            RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(ArqueoUtils.ModID, "pulverizing"));

    public static void bootstrap(Registerable<Enchantment> registerable) {
        var enchantments = registerable.getRegistryLookup(RegistryKeys.ENCHANTMENT);
        var items = registerable.getRegistryLookup(RegistryKeys.ITEM);

        register(registerable, MAGNET_POWER, Enchantment.builder(Enchantment.definition(
                        RegistryEntryList.of(ModItems.MAGNET.getRegistryEntry()),
                        5,
                        3,
                        Enchantment.leveledCost(5, 7),
                        Enchantment.leveledCost(25, 9),
                        2))
                .addEffect(EnchantmentEffectComponentTypes.TICK,
                        new MagnetPowerEnchantmentEffect())
        );

        register(registerable, MAGNET_RADIUS, Enchantment.builder(Enchantment.definition(
                        RegistryEntryList.of(ModItems.MAGNET.getRegistryEntry()),
                        5,
                        3,
                        Enchantment.leveledCost(5, 7),
                        Enchantment.leveledCost(25, 9),
                        2))
                .addEffect(EnchantmentEffectComponentTypes.TICK,
                        new MagnetRadiusEnchantmentEffect())
        );

        var pickaxes = items.getOrThrow(ItemTags.PICKAXES);
        register(registerable, PULVERIZING, Enchantment.builder(Enchantment.definition(
                        pickaxes,
                        2,
                        1,
                        Enchantment.leveledCost(15, 9),
                        Enchantment.leveledCost(65, 9),
                        6))
                .exclusiveSet(RegistryEntryList.of(enchantments.getOrThrow(Enchantments.SILK_TOUCH)))
                .addEffect(EnchantmentEffectComponentTypes.HIT_BLOCK, new PulverizingEnchantmentEffect())
        );
    }


    private static void register(Registerable<Enchantment> registry, RegistryKey<Enchantment> key, Enchantment.Builder builder) {
        registry.register(key, builder.build(key.getValue()));
    }
}

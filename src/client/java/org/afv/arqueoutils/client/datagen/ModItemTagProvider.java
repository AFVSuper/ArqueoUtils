package org.afv.arqueoutils.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import org.afv.arqueoutils.init.ModItems;
import org.afv.arqueoutils.init.ModTags;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.@NotNull WrapperLookup wrapperLookup) {
        valueLookupBuilder(ModTags.Items.REPAIRS_LUMINIS_ARMOR)
                .add(ModItems.LUMINIS_CORE);

        valueLookupBuilder(ItemTags.TRIMMABLE_ARMOR)
                .add(ModItems.LUMINIS_HELMET)
                .add(ModItems.LUMINIS_CHESTPLATE)
                .add(ModItems.LUMINIS_LEGGINGS)
                .add(ModItems.LUMINIS_BOOTS);

        valueLookupBuilder(ItemTags.DYEABLE)
                .add(ModItems.LUMINIS_HELMET)
                .add(ModItems.LUMINIS_CHESTPLATE)
                .add(ModItems.LUMINIS_LEGGINGS)
                .add(ModItems.LUMINIS_BOOTS);

        //<editor-fold desc="Scroll Item Tags">
        valueLookupBuilder(ModTags.Items.SCROLL_UPGRADE_ITEMS)
                .add(Items.SHIELD)
                .add(Items.WOODEN_AXE)
                .add(Items.WOODEN_SPEAR)
                .add(Items.WOODEN_SWORD)
                .add(Items.STONE_AXE)
                .add(Items.STONE_SPEAR)
                .add(Items.STONE_SWORD)
                .add(Items.LEATHER_HELMET)
                .add(Items.LEATHER_CHESTPLATE)
                .add(Items.LEATHER_LEGGINGS)
                .add(Items.LEATHER_BOOTS)
                .add(Items.CHAINMAIL_HELMET)
                .add(Items.CHAINMAIL_CHESTPLATE)
                .add(Items.CHAINMAIL_LEGGINGS)
                .add(Items.CHAINMAIL_BOOTS)
                .add(Items.COPPER_AXE)
                .add(Items.COPPER_SPEAR)
                .add(Items.COPPER_SWORD)
                .add(Items.COPPER_HELMET)
                .add(Items.COPPER_CHESTPLATE)
                .add(Items.COPPER_LEGGINGS)
                .add(Items.COPPER_BOOTS)
                .add(Items.IRON_AXE)
                .add(Items.IRON_SPEAR)
                .add(Items.IRON_SWORD)
                .add(Items.IRON_HELMET)
                .add(Items.IRON_CHESTPLATE)
                .add(Items.IRON_LEGGINGS)
                .add(Items.IRON_BOOTS)
                .add(Items.GOLDEN_AXE)
                .add(Items.GOLDEN_SPEAR)
                .add(Items.GOLDEN_SWORD)
                .add(Items.GOLDEN_HELMET)
                .add(Items.GOLDEN_CHESTPLATE)
                .add(Items.GOLDEN_LEGGINGS)
                .add(Items.GOLDEN_BOOTS)
                .add(Items.DIAMOND_AXE)
                .add(Items.DIAMOND_SPEAR)
                .add(Items.DIAMOND_SWORD)
                .add(Items.DIAMOND_HELMET)
                .add(Items.DIAMOND_CHESTPLATE)
                .add(Items.DIAMOND_LEGGINGS)
                .add(Items.DIAMOND_BOOTS)
                .add(Items.NETHERITE_AXE)
                .add(Items.NETHERITE_SPEAR)
                .add(Items.NETHERITE_SWORD)
                .add(Items.NETHERITE_HELMET)
                .add(Items.NETHERITE_CHESTPLATE)
                .add(Items.NETHERITE_LEGGINGS)
                .add(Items.NETHERITE_BOOTS)
                .add(Items.TRIDENT)
                .add(Items.MACE)
                .add(ModItems.WIND_TRIDENT)
                .add(ModItems.LUMINIS_HELMET)
                .add(ModItems.LUMINIS_CHESTPLATE)
                .add(ModItems.LUMINIS_LEGGINGS)
                .add(ModItems.LUMINIS_BOOTS)
                .add(ModItems.BLOOD_SCYTHE);

        valueLookupBuilder(ModTags.Items.SCROLL_DAMAGE_ITEMS)
                .add(Items.WOODEN_AXE)
                .add(Items.WOODEN_SWORD)
                .add(Items.WOODEN_SPEAR)
                .add(Items.STONE_AXE)
                .add(Items.STONE_SPEAR)
                .add(Items.STONE_SWORD)
                .add(Items.COPPER_AXE)
                .add(Items.COPPER_SPEAR)
                .add(Items.COPPER_SWORD)
                .add(Items.IRON_AXE)
                .add(Items.IRON_SPEAR)
                .add(Items.IRON_SWORD)
                .add(Items.GOLDEN_AXE)
                .add(Items.GOLDEN_SPEAR)
                .add(Items.GOLDEN_SWORD)
                .add(Items.DIAMOND_AXE)
                .add(Items.DIAMOND_SPEAR)
                .add(Items.DIAMOND_SWORD)
                .add(Items.NETHERITE_AXE)
                .add(Items.NETHERITE_SPEAR)
                .add(Items.NETHERITE_SWORD)
                .add(Items.TRIDENT)
                .add(ModItems.WIND_TRIDENT)
                .add(Items.MACE)
                .add(ModItems.BLOOD_SCYTHE);

        valueLookupBuilder(ModTags.Items.SCROLL_ARMOR_ITEMS)
                .add(Items.SHIELD)
                .add(Items.LEATHER_HELMET)
                .add(Items.LEATHER_CHESTPLATE)
                .add(Items.LEATHER_LEGGINGS)
                .add(Items.LEATHER_BOOTS)
                .add(Items.CHAINMAIL_HELMET)
                .add(Items.CHAINMAIL_CHESTPLATE)
                .add(Items.CHAINMAIL_LEGGINGS)
                .add(Items.CHAINMAIL_BOOTS)
                .add(Items.COPPER_HELMET)
                .add(Items.COPPER_CHESTPLATE)
                .add(Items.COPPER_LEGGINGS)
                .add(Items.COPPER_BOOTS)
                .add(Items.IRON_HELMET)
                .add(Items.IRON_CHESTPLATE)
                .add(Items.IRON_LEGGINGS)
                .add(Items.IRON_BOOTS)
                .add(Items.GOLDEN_HELMET)
                .add(Items.GOLDEN_CHESTPLATE)
                .add(Items.GOLDEN_LEGGINGS)
                .add(Items.GOLDEN_BOOTS)
                .add(Items.DIAMOND_HELMET)
                .add(Items.DIAMOND_CHESTPLATE)
                .add(Items.DIAMOND_LEGGINGS)
                .add(Items.DIAMOND_BOOTS)
                .add(Items.NETHERITE_HELMET)
                .add(Items.NETHERITE_CHESTPLATE)
                .add(Items.NETHERITE_LEGGINGS)
                .add(Items.NETHERITE_BOOTS)
                .add(ModItems.LUMINIS_HELMET)
                .add(ModItems.LUMINIS_CHESTPLATE)
                .add(ModItems.LUMINIS_LEGGINGS)
                .add(ModItems.LUMINIS_BOOTS);

        valueLookupBuilder(ModTags.Items.SCROLL_SPEED_ITEMS)
                .add(Items.LEATHER_BOOTS)
                .add(Items.CHAINMAIL_BOOTS)
                .add(Items.COPPER_BOOTS)
                .add(Items.IRON_BOOTS)
                .add(Items.GOLDEN_BOOTS)
                .add(Items.DIAMOND_BOOTS)
                .add(Items.NETHERITE_BOOTS)
                .add(ModItems.LUMINIS_BOOTS);

        valueLookupBuilder(ModTags.Items.SCROLL_OXYGEN_ITEMS)
                .add(Items.LEATHER_HELMET)
                .add(Items.CHAINMAIL_HELMET)
                .add(Items.COPPER_HELMET)
                .add(Items.IRON_HELMET)
                .add(Items.GOLDEN_HELMET)
                .add(Items.DIAMOND_HELMET)
                .add(Items.NETHERITE_HELMET)
                .add(ModItems.LUMINIS_HELMET);

        valueLookupBuilder(ModTags.Items.SCROLL_RANGE_ITEMS);
        //</editor-fold>
    }
}

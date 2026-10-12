package org.afv.arqueoutils.events;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.init.ModItems;
import org.afv.arqueoutils.util.SmeltLootFunction;

import java.util.List;

public class LootEvents {
    private static boolean firstVault = true;
    private static boolean firstSkull = true;

    public static void register() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            tableBuilder.apply(new SmeltLootFunction());
        });

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (LootTables.END_CITY_TREASURE_CHEST.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.25f))
                        .with(ItemEntry.builder(ModItems.ALCHEMIST_TOME));
                tableBuilder.pool(poolBuilder.build());
            }

            if (LootTables.ABANDONED_MINESHAFT_CHEST.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.2f))
                        .with(ItemEntry.builder(ModItems.SILVER_PLATES));
                tableBuilder.pool(poolBuilder.build());
            }

            if (LootTables.BASTION_TREASURE_CHEST.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.75f))
                        .with(ItemEntry.builder(ModItems.BASTION_TREASURE));
                tableBuilder.pool(poolBuilder.build());
            }

            if (LootTables.BASTION_OTHER_CHEST.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.05f))
                        .with(ItemEntry.builder(ModItems.BASTION_TREASURE));
                tableBuilder.pool(poolBuilder.build());
            }

            if (LootTables.NETHER_BRIDGE_CHEST.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.1f))
                        .with(ItemEntry.builder(ModItems.BLAZING_HEART));
                tableBuilder.pool(poolBuilder.build());
            }

            if (LootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_UNIQUE_CHEST.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.05f))
                        .with(ItemEntry.builder(ModItems.WIND_RELIC));
                tableBuilder.pool(poolBuilder.build());
            }

            Identifier lootTableId = key.getValue();
            String lootTablePath = lootTableId.getPath();
            boolean isStructureChest = lootTablePath.startsWith("chests/");
            boolean isArchaeology = lootTablePath.contains("archaeology");
            boolean isTrialVault = lootTablePath.contains("trial_chambers") && lootTablePath.contains("vault");
            boolean isTrialPots = lootTablePath.contains("trial_chambers") && lootTablePath.contains("pots");

            if (isStructureChest || isTrialVault) {
                LootPool.Builder scrollPoolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.25f))
                        .with(ItemEntry.builder(ModItems.SCROLL));
                tableBuilder.pool(scrollPoolBuilder.build());
            } else if (isTrialPots) {
                tableBuilder.pools(List.of()); // remove all existing pools

                LootPool.Builder pool = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.1f))
                        .with(ItemEntry.builder(ModItems.SCROLL));

                tableBuilder.pool(pool.build());
            } else if (isArchaeology) {
                tableBuilder.pools(List.of()); // remove all existing pools

                LootPool.Builder pool = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.25f))
                        .with(ItemEntry.builder(ModItems.SCROLL));

                tableBuilder.pool(pool.build());
            }
        });
    }
}

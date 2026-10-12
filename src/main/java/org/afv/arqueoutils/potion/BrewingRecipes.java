package org.afv.arqueoutils.potion;

import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;

public class BrewingRecipes {
    public static void init() {
        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
            builder.registerPotionRecipe(Potions.AWKWARD, Items.RAW_GOLD, ModPotions.HASTE_POTION);
            builder.registerPotionRecipe(ModPotions.HASTE_POTION, Items.REDSTONE, ModPotions.LONG_HASTE_POTION);
            builder.registerPotionRecipe(ModPotions.HASTE_POTION, Items.GLOWSTONE_DUST, ModPotions.STRONG_HASTE_POTION);

            builder.registerPotionRecipe(Potions.AWKWARD, Items.WITHER_ROSE, ModPotions.WITHER_POTION);
            builder.registerPotionRecipe(Potions.NIGHT_VISION, Items.INK_SAC, ModPotions.BLINDNESS_POTION);
            builder.registerPotionRecipe(Potions.WATER_BREATHING, Items.FERMENTED_SPIDER_EYE, ModPotions.NAUSEA_POTION);
            // builder.registerPotionRecipe(Potions.STRONG_HEALING, Items.DIAMOND, ModPotions.PANIC_POTION);

            builder.registerPotionRecipe(Potions.WEAKNESS, Items.RAW_COPPER, ModPotions.OXIDATION_POTION);
            builder.registerPotionRecipe(Potions.LONG_WEAKNESS, Items.RAW_COPPER, ModPotions.OXIDATION_POTION);

            builder.registerPotionRecipe(Potions.STRONG_HARMING, Items.GOLDEN_APPLE, ModPotions.HEALTH_DISRUPTION_POTION);
            builder.registerPotionRecipe(ModPotions.HEALTH_DISRUPTION_POTION, Items.REDSTONE, ModPotions.LONG_HEALTH_DISRUPTION_POTION);

            builder.registerPotionRecipe(Potions.INVISIBILITY, Items.INK_SAC, Potions.INVISIBILITY);
            builder.registerPotionRecipe(Potions.LONG_INVISIBILITY, Items.INK_SAC, Potions.LONG_INVISIBILITY);
        });
    }
}

package org.afv.arqueoutils.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.recipe.SmithingTransformRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import org.afv.arqueoutils.init.ModItems;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected @NotNull RecipeGenerator getRecipeGenerator(RegistryWrapper.@NotNull WrapperLookup wrapperLookup, @NotNull RecipeExporter recipeExporter) {
        return new RecipeGenerator(wrapperLookup, recipeExporter) {
            @Override
            public void generate() {
                createShaped(RecipeCategory.MISC, ModItems.MODEL_CARD)
                        .pattern("IDI")
                        .pattern("IPI")
                        .pattern("IDI")
                        .input('P', Items.PAPER)
                        .input('D', Items.DIAMOND)
                        .input('I', Items.IRON_INGOT)
                        .criterion(hasItem(Items.DIAMOND), conditionsFromItem(Items.DIAMOND))
                        .criterion(hasItem(Items.PAPER), conditionsFromItem(Items.PAPER))
                        .offerTo(recipeExporter);

                createShaped(RecipeCategory.MISC, ModItems.EMPTY_TRIM)
                        .pattern("SMS")
                        .pattern("SIS")
                        .pattern("SMS")
                        .input('S', Items.STONE)
                        .input('M', Items.SMOOTH_STONE)
                        .input('I', Items.IRON_INGOT)
                        .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                        .criterion(hasItem(Items.STONE), conditionsFromItem(Items.STONE))
                        .offerTo(recipeExporter);

                createShaped(RecipeCategory.TOOLS, ModItems.MAGNET)
                        .pattern("N N")
                        .pattern("I I")
                        .pattern("IRI")
                        .input('N', Items.NETHERITE_INGOT)
                        .input('R', Items.REDSTONE)
                        .input('I', Items.IRON_INGOT)
                        .criterion(hasItem(Items.NETHERITE_INGOT), conditionsFromItem(Items.NETHERITE_INGOT))
                        .offerTo(recipeExporter);

                createShaped(RecipeCategory.TOOLS, ModItems.ORE_POWDER_BAG)
                        .pattern(" S ")
                        .pattern("LPL")
                        .pattern(" L ")
                        .input('L', Items.LEATHER)
                        .input('S', Items.STRING)
                        .input('P', ModItems.ORE_POWDER)
                        .criterion(hasItem(ModItems.ORE_POWDER), conditionsFromItem(ModItems.ORE_POWDER))
                        .offerTo(recipeExporter);

                createShaped(RecipeCategory.COMBAT, ModItems.VOID_TOTEM)
                        .pattern("DCD")
                        .pattern("CTC")
                        .pattern("DCD")
                        .input('D', Items.DRAGON_BREATH)
                        .input('C', Items.CHORUS_FLOWER)
                        .input('T', Items.TOTEM_OF_UNDYING)
                        .criterion(hasItem(Items.TOTEM_OF_UNDYING), conditionsFromItem(Items.TOTEM_OF_UNDYING))
                        .criterion(hasItem(Items.DRAGON_BREATH), conditionsFromItem(Items.DRAGON_BREATH))
                        .offerTo(recipeExporter);

                createShaped(RecipeCategory.TOOLS, ModItems.EMPTY_AMULET)
                        .pattern("INI")
                        .pattern("NSN")
                        .pattern("INI")
                        .input('S', Items.NETHER_STAR)
                        .input('N', Items.NETHERITE_INGOT)
                        .input('I', Items.IRON_INGOT)
                        .criterion(hasItem(Items.NETHER_STAR), conditionsFromItem(Items.NETHER_STAR))
                        .offerTo(recipeExporter);

                createShaped(RecipeCategory.MISC, ModItems.LUMINIS_CORE)
                        .pattern("NQN")
                        .pattern("QQQ")
                        .pattern("NQN")
                        .input('N', Items.NETHERITE_SCRAP)
                        .input('Q', Items.QUARTZ)
                        .criterion(hasItem(Items.NETHERITE_SCRAP), conditionsFromItem(Items.NETHERITE_SCRAP))
                        .offerTo(recipeExporter);

                SmithingTransformRecipeJsonBuilder.create(
                                Ingredient.ofItem(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                                Ingredient.ofItem(Items.DIAMOND_HELMET),
                                Ingredient.ofItem(ModItems.LUMINIS_CORE),
                                RecipeCategory.COMBAT,
                                ModItems.LUMINIS_HELMET
                        ).criterion(hasItem(ModItems.LUMINIS_CORE), conditionsFromItem(ModItems.LUMINIS_CORE))
                        .offerTo(recipeExporter, "luminis_helmet_recipe");

                SmithingTransformRecipeJsonBuilder.create(
                                Ingredient.ofItem(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                                Ingredient.ofItem(Items.DIAMOND_CHESTPLATE),
                                Ingredient.ofItem(ModItems.LUMINIS_CORE),
                                RecipeCategory.COMBAT,
                                ModItems.LUMINIS_CHESTPLATE
                        ).criterion(hasItem(ModItems.LUMINIS_CORE), conditionsFromItem(ModItems.LUMINIS_CORE))
                        .offerTo(recipeExporter, "luminis_chestplate_recipe");

                SmithingTransformRecipeJsonBuilder.create(
                                Ingredient.ofItem(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                                Ingredient.ofItem(Items.DIAMOND_LEGGINGS),
                                Ingredient.ofItem(ModItems.LUMINIS_CORE),
                                RecipeCategory.COMBAT,
                                ModItems.LUMINIS_LEGGINGS
                        ).criterion(hasItem(ModItems.LUMINIS_CORE), conditionsFromItem(ModItems.LUMINIS_CORE))
                        .offerTo(recipeExporter, "luminis_leggings_recipe");

                SmithingTransformRecipeJsonBuilder.create(
                                Ingredient.ofItem(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                                Ingredient.ofItem(Items.DIAMOND_BOOTS),
                                Ingredient.ofItem(ModItems.LUMINIS_CORE),
                                RecipeCategory.COMBAT,
                                ModItems.LUMINIS_BOOTS
                        ).criterion(hasItem(ModItems.LUMINIS_CORE), conditionsFromItem(ModItems.LUMINIS_CORE))
                        .offerTo(recipeExporter, "luminis_boots_recipe");

                SmithingTransformRecipeJsonBuilder.create(
                        Ingredient.ofItem(Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE),
                        Ingredient.ofItem(Items.TRIDENT),
                        Ingredient.ofItem(ModItems.WIND_RELIC),
                        RecipeCategory.COMBAT,
                        ModItems.WIND_TRIDENT
                ).criterion(hasItem(ModItems.WIND_RELIC), conditionsFromItem(ModItems.WIND_RELIC))
                        .offerTo(recipeExporter, "wind_trident_recipe");
            }
        };
    }

    @Override
    public String getName() {
        return "arqueoutils/recipe";
    }
}

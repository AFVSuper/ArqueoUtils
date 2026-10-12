package org.afv.arqueoutils.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.IngredientPlacement;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.recipe.input.SmithingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import org.afv.arqueoutils.init.ModItems;
import org.afv.arqueoutils.init.ModRecipes;

import java.util.Optional;

public class TrimRemovalRecipe implements SmithingRecipe {
    @Override
    public boolean matches(SmithingRecipeInput input, World world) {

        ItemStack template = input.getStackInSlot(0);
        ItemStack base = input.getStackInSlot(1);
        ItemStack addition = input.getStackInSlot(2);

        if (!template.isOf(ModItems.EMPTY_TRIM)) {
            return false;
        }

        if (!addition.isEmpty()) {
            return false;
        }

        if (!base.contains(DataComponentTypes.EQUIPPABLE)) {
            return false;
        }

        return base.contains(DataComponentTypes.TRIM);
    }

    @Override
    public ItemStack craft(SmithingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {

        ItemStack result = input.getStackInSlot(1).copy();

        result.remove(DataComponentTypes.TRIM);

        return result;
    }

    @Override
    public RecipeSerializer<? extends SmithingRecipe> getSerializer() {
        return ModRecipes.TRIM_REMOVAL_SERIALIZER;
    }

    @Override
    public IngredientPlacement getIngredientPlacement() {
        return IngredientPlacement.NONE;
    }

    @Override
    public Optional<Ingredient> template() {
        return Optional.of(
                Ingredient.ofItems(ModItems.EMPTY_TRIM)
        );
    }

    @Override
    public Ingredient base() {
        return Ingredient.ofItems(
                Items.LEATHER_HELMET,
                Items.LEATHER_CHESTPLATE,
                Items.LEATHER_LEGGINGS,
                Items.LEATHER_BOOTS,

                Items.CHAINMAIL_HELMET,
                Items.CHAINMAIL_CHESTPLATE,
                Items.CHAINMAIL_LEGGINGS,
                Items.CHAINMAIL_BOOTS,

                Items.IRON_HELMET,
                Items.IRON_CHESTPLATE,
                Items.IRON_LEGGINGS,
                Items.IRON_BOOTS,

                Items.GOLDEN_HELMET,
                Items.GOLDEN_CHESTPLATE,
                Items.GOLDEN_LEGGINGS,
                Items.GOLDEN_BOOTS,

                Items.DIAMOND_HELMET,
                Items.DIAMOND_CHESTPLATE,
                Items.DIAMOND_LEGGINGS,
                Items.DIAMOND_BOOTS,

                Items.NETHERITE_HELMET,
                Items.NETHERITE_CHESTPLATE,
                Items.NETHERITE_LEGGINGS,
                Items.NETHERITE_BOOTS,

                ModItems.LUMINIS_HELMET,
                ModItems.LUMINIS_CHESTPLATE,
                ModItems.LUMINIS_LEGGINGS,
                ModItems.LUMINIS_BOOTS,

                Items.TURTLE_HELMET
        );
    }

    @Override
    public Optional<Ingredient> addition() {
        return Optional.of(Ingredient.ofItems());
    }

    public static class Serializer implements RecipeSerializer<TrimRemovalRecipe> {

        public static final MapCodec<TrimRemovalRecipe> CODEC =
                MapCodec.unit(new TrimRemovalRecipe());

        public static final PacketCodec<RegistryByteBuf, TrimRemovalRecipe> PACKET_CODEC =
                PacketCodec.unit(new TrimRemovalRecipe());

        @Override
        public MapCodec<TrimRemovalRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, TrimRemovalRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
}
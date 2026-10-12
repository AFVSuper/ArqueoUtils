package org.afv.arqueoutils.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public class SmeltUtils {
    public static ItemStack trySmelt(ServerWorld world, ItemStack input, Vec3d origin) {
        SingleStackRecipeInput recipeInput = new SingleStackRecipeInput(input);

        Item item = input.getItem();

        Optional<RecipeEntry<SmeltingRecipe>> recipe =
                world.getRecipeManager()
                        .getFirstMatch(RecipeType.SMELTING, recipeInput, world);

        if (recipe.isEmpty()) {
            return input;
        }

        ItemStack result =
                recipe.get()
                        .value()
                        .craft(recipeInput, world.getRegistryManager());

        if (!result.isOf(item)) {
            BlockPos pos = new BlockPos((int) origin.x, (int) origin.y, (int) origin.z);
            world.playSound(null, pos, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.AMBIENT, 0.25f, 1.2f);
            world.spawnParticles(ParticleTypes.FLAME, origin.x, origin.y, origin.z, 10, 0.25, 0.25, 0.25, 0.01);
        }

        result.setCount(result.getCount() * input.getCount());
        return result;
    }
}

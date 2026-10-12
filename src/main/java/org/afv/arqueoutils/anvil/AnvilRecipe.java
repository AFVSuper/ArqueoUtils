package org.afv.arqueoutils.anvil;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.afv.arqueoutils.init.ModItems;

public enum AnvilRecipe {
    FORTUNE_AMULET(ModItems.EMPTY_AMULET, ModItems.PERFECT_JADE, ModItems.FORTUNE_AMULET),
    LOOTING_AMULET(ModItems.EMPTY_AMULET, ModItems.BASTION_TREASURE, ModItems.LOOTING_AMULET),
    SMELT_AMULET(ModItems.EMPTY_AMULET, ModItems.BLAZING_HEART, ModItems.SMELT_AMULET),
    POTION_AMULET(ModItems.EMPTY_AMULET, ModItems.ALCHEMIST_TOME, ModItems.POTION_AMULET),
    FORGE_AMULET(ModItems.EMPTY_AMULET, ModItems.SILVER_PLATES, ModItems.FORGE_AMULET),
    BLOOD_SCYTHE(Items.NETHERITE_SWORD, Items.REDSTONE, ModItems.BLOOD_SCYTHE);

    private final Item input1, input2, output;

    AnvilRecipe(Item input1, Item input2, Item output) {
        this.input1 = input1;
        this.input2 = input2;
        this.output = output;
    }

    public Item getInput1() {
        return input1;
    }

    public Item getInput2() {
        return input2;
    }

    public Item getOutput() {
        return output;
    }

    public static AnvilRecipe getByIngredients(Item input1, Item input2) {
        for (AnvilRecipe recipe : AnvilRecipe.values()) {
            if (recipe.getInput1().equals(input1) && recipe.getInput2().equals(input2)) return recipe;
        }
        return null;
    }
}

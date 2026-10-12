package org.afv.arqueoutils.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.afv.arqueoutils.init.ModComponents;

import java.util.List;

public class OrePowderItem extends Item {
    public static List<String> getTypes() {
        return OrePowderTypes.ORDER;
    }

    public OrePowderItem(Settings settings) {
        super(settings);
    }

    @Override
    public Text getName(ItemStack stack) {
        String type = stack.get(ModComponents.ORE_POWDER_TYPE);
        if (!OrePowderTypes.isValid(type)) return super.getName(stack);

        return Text.translatable("item.arqueoutils.ore_powder." + type)
                .append(" ")
                .append(Text.translatable("item.arqueoutils.ore_powder.powder"));
    }
}

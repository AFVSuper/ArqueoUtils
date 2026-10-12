package org.afv.arqueoutils.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.afv.arqueoutils.init.ModComponents;
import org.afv.arqueoutils.screen.OrePowderBagScreenHandler;

public class OrePowderBagItem extends Item {
    public OrePowderBagItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }
        user.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                (syncId, inv, p) -> new OrePowderBagScreenHandler(syncId, inv),
                Text.translatable(getTranslationKey())
        ));
        return ActionResult.SUCCESS;
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        stack.set(ModComponents.ORE_POWDER_BAG, OrePowderBagData.EMPTY);
        return stack;
    }
}

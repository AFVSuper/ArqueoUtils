package org.afv.arqueoutils.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.afv.arqueoutils.init.ModItems;

public class MagnetSlot extends Slot {
    private final PlayerEntity player;

    public MagnetSlot(PlayerEntity player, Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.player = player;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return stack.isOf(ModItems.MAGNET);
    }

    @Override
    public void setStack(ItemStack stack) {
        super.setStack(stack);

        if (!player.getEntityWorld().isClient() && stack.isOf(ModItems.MAGNET)) {
            // Play sound like equipping armor
            // ArqueoUtils.LOGGER.info("SET");
            player.getEntityWorld().playSound(null, player.getBlockPos(), SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE.value(), SoundCategory.PLAYERS, 1.0f, 1.5f);
        }
    }
}
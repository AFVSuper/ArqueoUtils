package org.afv.arqueoutils.inventory;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.afv.arqueoutils.holder.ElytraHolder;

public class ElytraSlot extends Slot {
    private final PlayerEntity player;
    private final PlayerScreenHandler handler;

    public ElytraSlot(PlayerScreenHandler handler, PlayerEntity player, Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.player = player;
        this.handler = handler;
    }

    @Override
    public boolean isEnabled() {
//        if (this.handler instanceof ElytraPropertyHolder holder) {
//            Property property = holder.arqueoutils$getElytraProperty();
//            if (property != null) {
//                // Property values are kept up to date by vanilla automatically!
//                return property.get() == 1;
//            }
//        }
        if (player instanceof ElytraHolder holder) {
//            ArqueoUtils.LOGGER.info("Holder Boolean: {}", holder.hasElytraAdvancement());
//            ArqueoUtils.LOGGER.info("Player: {}", player);
            return holder.hasElytraAdvancement();
        } else {
//            ArqueoUtils.LOGGER.info("No Holder: {}", player);
        }
        return false;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return isEnabled() && stack.contains(DataComponentTypes.GLIDER);
    }

    @Override
    public void setStack(ItemStack stack) {
        super.setStack(stack);
        if (!player.getEntityWorld().isClient() && stack.contains(DataComponentTypes.GLIDER)) {
            player.getEntityWorld().playSound(null, player.getBlockPos(), SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA.value(), SoundCategory.PLAYERS, 1.0f, 1.0f);
        }
    }

    public PlayerEntity getPlayer() {
        return this.player;
    }
}
package org.afv.arqueoutils.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;

public class InvHelper {
    public static boolean hasInventoryFull(PlayerEntity player) {
        PlayerInventory inv = player.getInventory();
        return inv.getEmptySlot() == -1;
    }
}

package org.afv.arqueoutils.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.afv.arqueoutils.util.DisguiseManager;

public class BloodScythe extends Item {
    public BloodScythe(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, net.minecraft.entity.player.PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!stack.isOf(this)) {
            return ActionResult.PASS;
        }

        // Disguise only toggles from offhand while sneaking.
        if (hand != Hand.OFF_HAND || !user.isSneaking()) {
            return ActionResult.PASS;
        }

        if (!world.isClient() && user instanceof ServerPlayerEntity serverPlayer) {
            DisguiseManager.toggle(serverPlayer);
        }

        return ActionResult.SUCCESS;
    }
}

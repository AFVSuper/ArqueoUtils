package org.afv.arqueoutils.mixin;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.afv.arqueoutils.ArqueoUtils;
import org.afv.arqueoutils.holder.ElytraHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void onGliderEquip(World world, PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        // ArqueoUtils.LOGGER.info("USE");
        if (!ArqueoUtils.ENABLED_COLYTRA) return;
        ItemStack stack = player.getStackInHand(hand);

        if (stack.contains(DataComponentTypes.GLIDER)) {
            if (!(player instanceof ElytraHolder holder)) {
                cir.setReturnValue(ActionResult.PASS);
                return;
            }
            if (!holder.hasElytraAdvancement()) {
                cir.setReturnValue(ActionResult.PASS);
                return;
            }

            PlayerInventory inv = player.getInventory();

            // Get whatever is currently in the custom slot
            ItemStack prev = inv.getStack(42).copy();

            // Place the new elytra into the custom slot
            inv.setStack(42, stack.copy());

            // Remove the elytra from the hand
            player.setStackInHand(hand, prev);

            world.playSound(null, player.getBlockPos(), SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA.value(), SoundCategory.PLAYERS, 1f, 1f);

            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }
}

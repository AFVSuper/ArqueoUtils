package org.afv.arqueoutils.client.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import org.afv.arqueoutils.inventory.ElytraSlot;
import org.afv.arqueoutils.inventory.MagnetSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {

    @Inject(method = "drawSlot", at = @At("HEAD"), cancellable = true)
    private void hideCustomSlotsInCreativeTabs(DrawContext context, Slot slot, int mouseX, int mouseY, CallbackInfo ci) {
        // Check if our custom slots are currently being rendered
        if (slot instanceof ElytraSlot || slot instanceof MagnetSlot) {
            Object screen = this;

            // If the active screen is the Creative Inventory Screen...
            if (screen instanceof CreativeInventoryScreen creative) {
                // Get the currently selected tab using the vanilla static tracker
                boolean isInv = creative.isInventoryTabSelected();

                // If the selected tab is NOT the Survival Inventory tab, cancel rendering!
                if (isInv) {
                    ci.cancel();
                }
            }
        }
    }
}
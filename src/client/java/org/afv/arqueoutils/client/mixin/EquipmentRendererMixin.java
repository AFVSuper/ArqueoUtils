package org.afv.arqueoutils.client.mixin;

import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentAsset;
import org.afv.arqueoutils.init.ModComponents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EquipmentRenderer.class)
public class EquipmentRendererMixin {

    // Replace this with your actual custom boolean check
    private boolean shouldHideArmorModel() {
        return true;
    }

    @Redirect(
            method = "render(Lnet/minecraft/client/render/entity/equipment/EquipmentModel$LayerType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/util/Identifier;II)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/equipment/EquipmentRenderer;getDyeColor(Lnet/minecraft/client/render/entity/equipment/EquipmentModel$Layer;I)I"
            )
    )
    private int redirectDyeColor(EquipmentModel.Layer layer, int dyeColor, EquipmentModel.LayerType layerType, net.minecraft.registry.RegistryKey<EquipmentAsset> assetKey, net.minecraft.client.model.Model<?> model, Object object, ItemStack itemStack) {
        if (itemStack.contains(ModComponents.INVISIBLE_DYED)) {
            // Returning 0 skips the model and glint logic in the loop completely,
            // allowing the code below it to render the trims normally.
            return 0;
        }

        // Otherwise, invoke the original behavior manually
        // Since getDyeColor is static on EquipmentRenderer, we call it directly:
        return EquipmentRendererInvoker.callGetDyeColor(layer, dyeColor);
    }
}
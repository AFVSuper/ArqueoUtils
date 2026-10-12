package org.afv.arqueoutils.client.mixin;

import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EquipmentRenderer.class)
public interface EquipmentRendererInvoker {
    
    @Invoker("getDyeColor")
    static int callGetDyeColor(EquipmentModel.Layer layer, int dyeColor) {
        throw new AssertionError(); // Mixin replaces this at runtime
    }
}
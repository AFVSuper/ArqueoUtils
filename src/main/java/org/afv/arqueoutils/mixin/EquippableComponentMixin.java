package org.afv.arqueoutils.mixin;

import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EquippableComponent.class)
public class EquippableComponentMixin {
    @Shadow
    @Final
    private EquipmentSlot slot;
}

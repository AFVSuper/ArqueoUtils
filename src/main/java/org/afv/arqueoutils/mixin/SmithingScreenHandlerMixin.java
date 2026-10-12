package org.afv.arqueoutils.mixin;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.SmithingScreenHandler;
import org.afv.arqueoutils.init.ModComponents;
import org.afv.arqueoutils.init.ModItems;
import org.afv.arqueoutils.util.EquipmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmithingScreenHandler.class)
public class SmithingScreenHandlerMixin {


    @Inject(method = "updateResult", at = @At("HEAD"), cancellable = true)
    private void arqueoutils$removeTrimOnEmptyTemplate(
            CallbackInfo ci
    ) {
        SmithingScreenHandler handler = (SmithingScreenHandler)(Object)this;

        ItemStack template = handler.getSlot(0).getStack();
        ItemStack base = handler.getSlot(1).getStack();
        ItemStack addition = handler.getSlot(2).getStack();

        if (template.isOf(ModItems.EMPTY_TRIM) && addition.isOf(Items.FLINT)) {
            if (base.contains(DataComponentTypes.TRIM)) {
                ItemStack result = base.copy();
                result.remove(DataComponentTypes.TRIM);

                handler.getSlot(handler.getResultSlotIndex()).setStack(result);

                ci.cancel(); // stop vanilla result logic
            } else {
                handler.getSlot(handler.getResultSlotIndex()).setStack(ItemStack.EMPTY);
                ci.cancel();
            }
        }
    }

    @Inject(method = "updateResult", at = @At("HEAD"), cancellable = true)
    private void arqueoutils$fixNetheriteUpgradeWithScroll(
            CallbackInfo ci
    ) {
        SmithingScreenHandler handler = (SmithingScreenHandler)(Object)this;

        ItemStack template = handler.getSlot(0).getStack();
        ItemStack base = handler.getSlot(1).getStack();
        ItemStack addition = handler.getSlot(2).getStack();

        if (template.isOf(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE) && addition.isOf(Items.NETHERITE_INGOT)) {
            Item mapItem = EquipmentHelper.mapNetheriteUpgrade(base);
            if (mapItem == Items.AIR) return;
            if (base.contains(ModComponents.SCROLL_UPGRADE)) {
                ItemStack result = base.copy();
                ItemStack newResult = new ItemStack(mapItem);

                AttributeModifiersComponent existingPrev =
                        result.getOrDefault(
                                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                                AttributeModifiersComponent.DEFAULT
                        );

                AttributeModifiersComponent existingNew =
                        newResult.getOrDefault(
                                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                                AttributeModifiersComponent.DEFAULT
                        );

                newResult = result.withItem(mapItem);
                newResult.remove(DataComponentTypes.ATTRIBUTE_MODIFIERS);

                AttributeModifiersComponent.Builder builder =
                        AttributeModifiersComponent.builder();

                for (AttributeModifiersComponent.Entry entry : existingNew.modifiers()) {
                    EntityAttributeModifier modifier = entry.modifier();
                    builder.add(
                            entry.attribute(),
                            modifier,
                            entry.slot()
                    );
                }

                for (AttributeModifiersComponent.Entry entry : existingPrev.modifiers()) {
                    if (!entry.modifier().id().getPath().startsWith("scroll_")) continue;
                    EntityAttributeModifier modifier = entry.modifier();
                    builder.add(
                            entry.attribute(),
                            modifier,
                            entry.slot(), AttributeModifiersComponent.Display.getHidden()
                    );
                }

                newResult.set(
                        DataComponentTypes.ATTRIBUTE_MODIFIERS,
                        builder.build()
                );

                handler.getSlot(handler.getResultSlotIndex()).setStack(newResult);

                ci.cancel(); // stop vanilla result logic
            }
        }
    }

    @Inject(method = "updateResult", at = @At("HEAD"), cancellable = true)
    private void arqueoutils$fixLuminisUpgradeWithScroll(
            CallbackInfo ci
    ) {
        SmithingScreenHandler handler = (SmithingScreenHandler)(Object)this;

        ItemStack template = handler.getSlot(0).getStack();
        ItemStack base = handler.getSlot(1).getStack();
        ItemStack addition = handler.getSlot(2).getStack();

        if (template.isOf(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE) && addition.isOf(ModItems.LUMINIS_CORE)) {
            Item mapItem = EquipmentHelper.mapLuminisUpgrade(base);
            if (mapItem == Items.AIR) return;
            if (base.contains(ModComponents.SCROLL_UPGRADE)) {
                ItemStack result = base.copy();
                ItemStack newResult = new ItemStack(mapItem);

                AttributeModifiersComponent existingPrev =
                        result.getOrDefault(
                                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                                AttributeModifiersComponent.DEFAULT
                        );

                AttributeModifiersComponent existingNew =
                        newResult.getOrDefault(
                                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                                AttributeModifiersComponent.DEFAULT
                        );

                newResult = result.withItem(mapItem);
                newResult.remove(DataComponentTypes.ATTRIBUTE_MODIFIERS);

                AttributeModifiersComponent.Builder builder =
                        AttributeModifiersComponent.builder();

                for (AttributeModifiersComponent.Entry entry : existingNew.modifiers()) {
                    EntityAttributeModifier modifier = entry.modifier();
                    builder.add(
                            entry.attribute(),
                            modifier,
                            entry.slot()
                    );
                }

                for (AttributeModifiersComponent.Entry entry : existingPrev.modifiers()) {
                    if (!entry.modifier().id().getPath().startsWith("scroll_")) continue;
                    EntityAttributeModifier modifier = entry.modifier();
                    builder.add(
                            entry.attribute(),
                            modifier,
                            entry.slot(), AttributeModifiersComponent.Display.getHidden()
                    );
                }

                newResult.set(
                        DataComponentTypes.ATTRIBUTE_MODIFIERS,
                        builder.build()
                );

                handler.getSlot(handler.getResultSlotIndex()).setStack(newResult);

                ci.cancel(); // stop vanilla result logic
            }
        }
    }
}
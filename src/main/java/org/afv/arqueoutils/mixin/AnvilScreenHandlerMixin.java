package org.afv.arqueoutils.mixin;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringHelper;
import org.afv.arqueoutils.anvil.AnvilRecipe;
import org.afv.arqueoutils.init.ModComponents;
import org.afv.arqueoutils.init.ModItems;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin {
    @Final
    @Shadow
    private Property levelCost;

    @Shadow
    private String newItemName;

    @Inject(method = "updateResult", at = @At("RETURN"), cancellable = true)
    private void onUpdateResultMiscAnvil(CallbackInfo ci) {
        AnvilScreenHandler handler = (AnvilScreenHandler)(Object)this;
        ItemStack left = handler.getStacks().get(0);
        ItemStack right = handler.getStacks().get(1);

        AnvilRecipe recipe = AnvilRecipe.getByIngredients(left.getItem(), right.getItem());
        if (recipe != null) {
            PlayerEntity player = ((ForgingScreenHandlerAccessor) handler).getPlayer();
            if (recipe == AnvilRecipe.BLOOD_SCYTHE && !player.getStringifiedName().equals("AFVSuper")) {
                return;
            }
            ItemStack result = new ItemStack(recipe.getOutput());
            ((ForgingScreenHandlerAccessor) handler).getOutput().setStack(0, result);

            int i = 0;
            if (this.newItemName != null && !StringHelper.isBlank(this.newItemName)) {
                if (!this.newItemName.equals(left.getName().getString())) {
                    i += 1;
                    result.set(DataComponentTypes.CUSTOM_NAME, Text.literal(this.newItemName));
                }
            } else if (left.contains(DataComponentTypes.CUSTOM_NAME)) {
                i += 1;
                result.remove(DataComponentTypes.CUSTOM_NAME);
            }

            levelCost.set(30 + i);

            ci.cancel();
        }
    }

    @Inject(method = "updateResult", at = @At("RETURN"), cancellable = true)
    private void onUpdateResultInvisibleInk(CallbackInfo ci) {
        AnvilScreenHandler handler = (AnvilScreenHandler)(Object)this;
        ItemStack left = handler.getStacks().get(0);
        ItemStack right = handler.getStacks().get(1);

        if (left.isIn(ItemTags.TRIMMABLE_ARMOR)
                && right.isOf(ModItems.INVISIBLE_INK)
                && !left.contains(ModComponents.INVISIBLE_DYED)) {
            ItemStack result = left.copy();
            result.set(ModComponents.INVISIBLE_DYED, true);
            ((ForgingScreenHandlerAccessor) handler).getOutput().setStack(0, result);

            int i = 0;
            if (this.newItemName != null && !StringHelper.isBlank(this.newItemName)) {
                if (!this.newItemName.equals(left.getName().getString())) {
                    i += 1;
                    result.set(DataComponentTypes.CUSTOM_NAME, Text.literal(this.newItemName));
                }
            } else if (left.contains(DataComponentTypes.CUSTOM_NAME)) {
                i += 1;
                result.remove(DataComponentTypes.CUSTOM_NAME);
            }
            levelCost.set(10 + i);

            ci.cancel();
        }
    }

    @Inject(method = "updateResult", at = @At("RETURN"), cancellable = true)
    private void onUpdateResultModelCard(CallbackInfo ci) {
        AnvilScreenHandler handler = (AnvilScreenHandler)(Object)this;
        ItemStack left = handler.getStacks().get(0);
        ItemStack right = handler.getStacks().get(1);

        if (right.isOf(ModItems.MODEL_CARD)) {
            ItemStack result = left.copy();
            Text customName = right.get(DataComponentTypes.CUSTOM_NAME);
            if (customName == null || customName.getString().equalsIgnoreCase("CLEAR")) {
                result.remove(DataComponentTypes.ITEM_MODEL);
            } else {
                String modelStrRaw = customName.getString();
                String modelStr;
                if (modelStrRaw.startsWith("all/")) {
                    modelStr = "all/" + modelStrRaw;
                } else {
                    PlayerEntity player = ((ForgingScreenHandlerAccessor) handler).getPlayer();
                    modelStr = player.getStringifiedName().toLowerCase() + "/" + modelStrRaw;
                }
                result.set(DataComponentTypes.ITEM_MODEL, Identifier.of("arqueoutils:modelcard/" + modelStr));
            }

            ((ForgingScreenHandlerAccessor) handler).getOutput().setStack(0, result);

            int i = 0;
            if (this.newItemName != null && !StringHelper.isBlank(this.newItemName)) {
                if (!this.newItemName.equals(left.getName().getString())) {
                    i += 1;
                    result.set(DataComponentTypes.CUSTOM_NAME, Text.literal(this.newItemName));
                }
            } else if (left.contains(DataComponentTypes.CUSTOM_NAME)) {
                i += 1;
                result.remove(DataComponentTypes.CUSTOM_NAME);
            }
            levelCost.set(10 + i);

            ci.cancel();
        }
    }

    @Redirect(
            method = "onTakeOutput",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/inventory/Inventory;setStack(ILnet/minecraft/item/ItemStack;)V",
                    ordinal = 2
            )
    )
    private void modifySlotAfterTake(Inventory instance, int i, ItemStack stack) {
        if (i != 1 || stack != ItemStack.EMPTY) return;
        ItemStack inputStack = instance.getStack(1);
        if (!inputStack.isOf(ModItems.MODEL_CARD)) inputStack.decrement(1);

        if (inputStack.isEmpty()) {
            instance.setStack(1, ItemStack.EMPTY);
        } else {
            instance.setStack(1, inputStack);
        }
    }
}

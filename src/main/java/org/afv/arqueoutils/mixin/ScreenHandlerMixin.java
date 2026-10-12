package org.afv.arqueoutils.mixin;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ClickType;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;
import org.afv.arqueoutils.init.*;
import org.afv.arqueoutils.items.ScrollItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;


@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {

    @Inject(
            method = "handleSlotClick",
            at = @At("HEAD"),
            cancellable = true
    )
    private void arqueoutils$applyScrollUpgrade(
            PlayerEntity player, ClickType clickType, Slot slot, ItemStack stack, ItemStack cursorStack, CallbackInfoReturnable<Boolean> cir
    ) {
        if (clickType != ClickType.RIGHT) {
            return; // Only right-click pickup
        }

        // Must have scroll on cursor
        if (cursorStack.isEmpty() || !cursorStack.isOf(ModItems.SCROLL)) {
            return;
        }

        // Must be valid equipment
        if (stack.isEmpty() || !stack.isIn(ModTags.Items.SCROLL_UPGRADE_ITEMS)) {
            return;
        }

        // --- Get scroll data ---
        String type = cursorStack.get(ModComponents.SCROLL_TYPE);
        Integer quality = cursorStack.get(ModComponents.SCROLL_QUALITY);

        if (type == null || quality == null) {
            return;
        }

        switch (type) {
            case "damage", "attack_speed" -> {
                if (!stack.isIn(ModTags.Items.SCROLL_DAMAGE_ITEMS)) {
                    player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BASS.value(), 1, 0.5f);
                    cir.setReturnValue(true);
                    return;
                }
            }
            case "armor", "toughness", "health", "experience" -> {
                if (!stack.isIn(ModTags.Items.SCROLL_ARMOR_ITEMS)) {
                    player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BASS.value(), 1, 0.5f);
                    cir.setReturnValue(true);
                    return;
                }
            }
            case "speed" -> {
                if (!stack.isIn(ModTags.Items.SCROLL_SPEED_ITEMS)) {
                    player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BASS.value(), 1, 0.5f);
                    cir.setReturnValue(true);
                    return;
                }
            }
            case "oxygen" -> {
                if (!stack.isIn(ModTags.Items.SCROLL_OXYGEN_ITEMS)) {
                    player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BASS.value(), 1, 0.5f);
                    cir.setReturnValue(true);
                    return;
                }
            }
            default -> {}
        }

        ModComponents.ScrollUpgrade currentUpgrade = stack.get(ModComponents.SCROLL_UPGRADE);
        if (currentUpgrade != null && currentUpgrade.type().equals(type) && currentUpgrade.quality() == quality) {
            player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BASS.value(), 1, 0.5f);
            cir.setReturnValue(true);
            return;
        }

        // --- Apply upgrade ---
        ModComponents.ScrollUpgrade upgrade =
                new ModComponents.ScrollUpgrade(type, quality);

        stack.set(ModComponents.SCROLL_UPGRADE, upgrade);

        AttributeModifiersComponent existing =
                stack.getOrDefault(
                        DataComponentTypes.ATTRIBUTE_MODIFIERS,
                        AttributeModifiersComponent.DEFAULT
                );

        AttributeModifiersComponent.Builder builder =
                AttributeModifiersComponent.builder();

        for (AttributeModifiersComponent.Entry entry : existing.modifiers()) {
            EntityAttributeModifier modifier = entry.modifier();
            if (!modifier.id().getPath().startsWith("scroll_")) {
                builder.add(
                        entry.attribute(),
                        modifier,
                        entry.slot()
                );
            }
        }

        AttributeModifiersComponent scrollModifiers =
                createModifiers(upgrade, stack.getItem());

        MinecraftServer server = player.getEntityWorld().getServer();
        if (server != null) {
            ModCriteria.APPLY_SCROLL.trigger(server.getPlayerManager().getPlayer(player.getUuid()), quality);
        }

        for (AttributeModifiersComponent.Entry entry : scrollModifiers.modifiers()) {
            builder.add(entry.attribute(), entry.modifier(), entry.slot()
                    , AttributeModifiersComponent.Display.getHidden()
            );
        }

        stack.set(
                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                builder.build()
        );

        // --- Consume scroll ---
        cursorStack.decrement(1);

        // --- Play sound ONLY for player ---
        player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA.value(), 1, 1.5f);
        player.playSound(SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1.2f);

        cir.setReturnValue(true); // Prevent normal right-click behavior
    }

    @Unique
    private static AttributeModifiersComponent createModifiers(ModComponents.ScrollUpgrade upgrade, Item item) {
        AttributeModifiersComponent.Builder builder = AttributeModifiersComponent.builder();

        double value = ScrollItem.getValue(upgrade.type(), upgrade.quality());

        switch (upgrade.type()) {
            case "damage" -> builder.add(
                    EntityAttributes.ATTACK_DAMAGE,
                    new EntityAttributeModifier(
                            Identifier.of(ArqueoUtils.ModID, "scroll_damage_" + UUID.randomUUID()),
                            value,
                            EntityAttributeModifier.Operation.ADD_VALUE
                    ),
                    AttributeModifierSlot.MAINHAND
            );
            case "armor" -> builder.add(
                    EntityAttributes.ARMOR,
                    new EntityAttributeModifier(
                            Identifier.of(ArqueoUtils.ModID, "scroll_armor_" + UUID.randomUUID()),
                            value,
                            EntityAttributeModifier.Operation.ADD_VALUE
                    ),
                    item == Items.SHIELD ? AttributeModifierSlot.HAND : AttributeModifierSlot.ARMOR
            );
            case "toughness" -> builder.add(
                    EntityAttributes.ARMOR_TOUGHNESS,
                    new EntityAttributeModifier(
                            Identifier.of(ArqueoUtils.ModID, "scroll_toughness_" + UUID.randomUUID()),
                            value,
                            EntityAttributeModifier.Operation.ADD_VALUE
                    ),
                    item == Items.SHIELD ? AttributeModifierSlot.HAND : AttributeModifierSlot.ARMOR
            );
            case "health" -> builder.add(
                    EntityAttributes.MAX_HEALTH,
                    new EntityAttributeModifier(
                            Identifier.of(ArqueoUtils.ModID, "scroll_health_" + UUID.randomUUID()),
                            value,
                            EntityAttributeModifier.Operation.ADD_VALUE
                    ),
                    item == Items.SHIELD ? AttributeModifierSlot.HAND : AttributeModifierSlot.ARMOR
            );
            case "speed" -> builder.add(
                    EntityAttributes.MOVEMENT_SPEED,
                    new EntityAttributeModifier(
                            Identifier.of(ArqueoUtils.ModID, "scroll_speed_" + UUID.randomUUID()),
                            value,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    ),
                    AttributeModifierSlot.FEET
            );
            case "attack_speed" -> builder.add(
                    EntityAttributes.ATTACK_SPEED,
                    new EntityAttributeModifier(
                            Identifier.of(ArqueoUtils.ModID, "scroll_attack_speed_" + UUID.randomUUID()),
                            value,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    ),
                    AttributeModifierSlot.MAINHAND
            );
            case "oxygen" -> builder.add(
                    EntityAttributes.OXYGEN_BONUS,
                    new EntityAttributeModifier(
                            Identifier.of(ArqueoUtils.ModID, "scroll_oxygen_" + UUID.randomUUID()),
                            value,
                            EntityAttributeModifier.Operation.ADD_VALUE
                    ),
                    AttributeModifierSlot.HEAD
            );
            case "experience" -> builder.add(
                    ModAttributes.EXPERIENCE_GAIN,
                    new EntityAttributeModifier(
                            Identifier.of(ArqueoUtils.ModID, "scroll_experience_" + UUID.randomUUID()),
                            value,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    ),
                    item == Items.SHIELD ? AttributeModifierSlot.HAND : AttributeModifierSlot.ARMOR
            );
        }

        return builder.build();
    }
}
package org.afv.arqueoutils.mixin;

import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.random.Random;
import org.afv.arqueoutils.init.ModComponents;
import org.afv.arqueoutils.init.ModItems;
import org.afv.arqueoutils.init.ModTags;
import org.afv.arqueoutils.items.*;
import org.afv.arqueoutils.util.ColorUtil;
import org.afv.arqueoutils.util.EquipmentHelper;
import org.afv.arqueoutils.util.SoulboundUtil;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(
            method = "appendTooltip",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;appendComponentTooltip(Lnet/minecraft/component/ComponentType;Lnet/minecraft/item/Item$TooltipContext;Lnet/minecraft/component/type/TooltipDisplayComponent;Ljava/util/function/Consumer;Lnet/minecraft/item/tooltip/TooltipType;)V",
                    ordinal = 14
            )
    )
    public void addTopTooltip(Item.TooltipContext context, TooltipDisplayComponent displayComponent, @Nullable PlayerEntity player, TooltipType type, Consumer<Text> textConsumer, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;

        if (SoulboundUtil.isStackSoulbound(stack)) {
            textConsumer.accept(Text.translatable("component.arqueoutils.soulbound")
                    .withColor(0x986bbf));
        }

        if (stack.isOf(ModItems.ORE_POWDER_BAG)) {
            OrePowderBagData contents = stack.get(ModComponents.ORE_POWDER_BAG);
            if (contents != null) {
                int[] m = new int[]{0, 0, 0};
                for (int c = 0; c < 3; c++) {
                    for (int r = 0; r < 3; r++) {
                        int s = ("" + contents.get(r * 3 + c)).length();
                        if (s > m[c]) m[c] = s;
                    }
                }
                for (int r = 0; r < 3; r++) {
                    MutableText line = Text.empty();
                    for (int c = 0; c < 3; c++) {
                        int idx = r * 3 + c;
                        long count = contents.get(idx);
                        line.append(Text.literal("\uef00".repeat(m[c] - ("" + count).length()) + count)
                                .withColor(OrePowderTypes.getColor(OrePowderTypes.ORDER.get(idx))));
                        if (c < 2) line.append(Text.literal(" | ")
                                .formatted(Formatting.DARK_GRAY));
                    }
                    textConsumer.accept(line);
                }
            }
        }

        if (stack.isIn(ModTags.Items.SCROLL_UPGRADE_ITEMS)) {
            ModComponents.ScrollUpgrade upgrade = stack.get(ModComponents.SCROLL_UPGRADE);
            textConsumer.accept(Text.translatable("tooltip.arqueoutils.scroll.upgrade")
                    .formatted(Formatting.GRAY));
            if (upgrade == null) {
                textConsumer.accept(Text.literal(" ")
                        .append(Text.translatable("tooltip.arqueoutils.scroll.upgrade.null")
                                .formatted(Formatting.DARK_GRAY)));
            } else {
                String upgrade_type = upgrade.type();
                int upgrade_quality = upgrade.quality();
                String roman = "I";
                switch (upgrade_quality) {
                    case 2 -> roman = "II";
                    case 3 -> roman = "III";
                    case 4 -> roman = "IV";
                    default -> {}
                }
                double boost = Math.floor(ScrollItem.getValue(upgrade_type, upgrade_quality) * 10000) / 10000;
                String s;

                if (upgrade_type.equalsIgnoreCase("attack_speed")
                        || upgrade_type.equalsIgnoreCase("experience")
                        || upgrade_type.equalsIgnoreCase("speed")) {
                    boost *= 100;
                    if (boost == (int)boost) {
                        s = String.valueOf((int)boost); // remove .0 for whole numbers
                    } else {
                        s = String.valueOf(boost);      // keep decimals for fractions
                    }
                    s = s + "%";
                } else {
                    if (boost == (int)boost) {
                        s = String.valueOf((int)boost); // remove .0 for whole numbers
                    } else {
                        s = String.valueOf(boost);      // keep decimals for fractions
                    }
                }
                int color = ScrollItem.getColorByType(upgrade_type);
                textConsumer.accept(Text.literal(" ")
                        .append(Text.translatable("tooltip.arqueoutils.scroll." + upgrade_type))
                        .append(" ")
                        .append(Text.literal(roman))
                        .withColor(color));
                textConsumer.accept(Text.literal(" +")
                        .append(Text.literal(s))
                        .append(" ")
                        .append(Text.translatable("tooltip.arqueoutils.scroll." + upgrade_type + ".grants"))
                        .withColor(color));
            }
        }

        if (stack.contains(ModComponents.INVISIBLE_DYED)) {
            textConsumer.accept(Text.translatable("tooltip.arqueoutils.invis_dyed")
                    .formatted(Formatting.GRAY)
                    .formatted(Formatting.ITALIC));
        }
    }

    private static double getBoost(String upgrade_type, int upgrade_quality) {
        double boost = 0;
        switch (upgrade_type) {
            case "damage", "oxygen", "armor" -> {
                boost = 0.25 * upgrade_quality;
            }
            case "toughness" -> {
                boost = 0.125 * upgrade_quality;
            }
            case "health" -> {
                boost = 0.5 * upgrade_quality;
            }
            case "speed", "attack_speed", "experience" -> {
                boost = 2.5 * upgrade_quality;
            }
            default -> {}
        }
        return boost;
    }

    @Inject(
            method = "appendTooltip",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/tooltip/TooltipType;isAdvanced()Z"
            )
    )
    public void addTooltip(Item.TooltipContext context, TooltipDisplayComponent displayComponent, @Nullable PlayerEntity player, TooltipType type, Consumer<Text> textConsumer, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;

        if (stack.getItem() instanceof ScrollItem scroll) {
            scroll.addTooltipForScroll(stack, textConsumer);
        }

        if (stack.getItem() instanceof AmuletItem amulet) {
            amulet.addTooltipForAmulet(stack, textConsumer);
        }

        if (stack.isOf(ModItems.MAGNET)) {
            double power = MagnetItem.getPower(stack);
            int radius = MagnetItem.getRadius(stack);

            textConsumer.accept(Text.empty());
            textConsumer.accept(
                    Text.translatable("item.modifiers.any")
                            .formatted(Formatting.GRAY)
            );
            textConsumer.accept(
                    Text.literal(" " + power + " ")
                            .formatted(Formatting.DARK_GREEN)
                            .append(
                                    Text.translatable("tooltip.arqueoutils.magnet.power")
                            ).formatted(Formatting.DARK_GREEN)
            );
            textConsumer.accept(
                    Text.literal(" " + radius + " ")
                            .formatted(Formatting.DARK_GREEN)
                            .append(
                                    Text.translatable("tooltip.arqueoutils.magnet.radius")
                            ).formatted(Formatting.DARK_GREEN)
            );
        }

        if (stack.isDamageable()) {
            int maxDamage = stack.getMaxDamage();
            int damage = stack.getDamage();
            float f = (float) damage / maxDamage;
            int color;

            if (f < 0.2f) {
                color = ColorUtil.lerpColor(
                        0x32bf2a,
                        0x71ff69,
                        f / 0.2f
                );
            } else if (f < 0.5f) {
                color = ColorUtil.lerpColor(
                        0x71ff69,
                        0xf8ff69,
                        (f - 0.2f) / 0.3f
                );
            } else if (f < 0.8f) {
                color = ColorUtil.lerpColor(
                        0xf8ff69,
                        0xff7d69,
                        (f - 0.5f) / 0.3f
                );
            } else {
                color = ColorUtil.lerpColor(
                        0xff7d69,
                        0xff3b3b,
                        (f - 0.8f) / 0.2f
                );
            }
            textConsumer.accept(Text.empty());
            textConsumer.accept(Text.empty()
                    .append(Text.translatable("tooltip.arqueoutils.durability"))
                    .append(": ")
                    .append(Text.literal("" + (maxDamage-damage))
                            .withColor(color))
                    .append(Text.literal(" / "+maxDamage))
            );
        }
    }

    @Inject(
            method = "damage(ILnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/EquipmentSlot;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void damage3(
            int amount, LivingEntity entity, EquipmentSlot slot, CallbackInfo ci
    ) {
        // ArqueoUtils.LOGGER.info("Damage 3");
        if (!EquipmentHelper.hasForgeAmulet(entity)) return;
        Random random = entity.getRandom();
        if (random.nextFloat() < 0.25) {
            ci.cancel();
        }
    }

    @Redirect(
            method = "appendTooltip",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V",
                    ordinal = 4
            )
    )
    private void arqueoutils$removeDurabilityLine(
            Consumer<Text> consumer,
            Object text
    ) {
        // Do nothing
    }
}

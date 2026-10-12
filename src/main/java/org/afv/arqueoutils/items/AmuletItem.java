package org.afv.arqueoutils.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import org.afv.arqueoutils.init.ModComponents;

import java.util.function.Consumer;

public class AmuletItem extends Item {
    private final String type;

    public AmuletItem(String type, Item.Settings settings) {
        super(settings.maxCount(1).rarity(type.equals("empty") ? Rarity.UNCOMMON : Rarity.EPIC)
                .component(ModComponents.SOULBOUND, !type.equals("empty")));
        this.type = type;
    }

    public String getType() { return type; }

    public void addTooltipForAmulet(ItemStack stack, Consumer<Text> textConsumer) {
        switch (this.type) {
            case "fortune" -> {
                textConsumer.accept(Text.empty());
                textConsumer.accept(Text.translatable("item.modifiers.offhand")
                        .formatted(Formatting.GRAY));
                textConsumer.accept(Text.literal(" +1 ")
                        .append(Text.translatable("tooltip.arqueoutils.amulet.fortune"))
                        .formatted(Formatting.DARK_GREEN));
            }
            case "looting" -> {
                textConsumer.accept(Text.empty());
                textConsumer.accept(Text.translatable("item.modifiers.offhand")
                        .formatted(Formatting.GRAY));
                textConsumer.accept(Text.literal(" +1 ")
                        .append(Text.translatable("tooltip.arqueoutils.amulet.looting"))
                        .formatted(Formatting.DARK_GREEN));
            }
            case "smelt" -> {
                textConsumer.accept(Text.empty());
                textConsumer.accept(Text.translatable("item.modifiers.offhand")
                        .formatted(Formatting.GRAY));
                textConsumer.accept(Text.literal(" ")
                        .append(Text.translatable("tooltip.arqueoutils.amulet.smelt1"))
                        .formatted(Formatting.DARK_GREEN));
                textConsumer.accept(Text.literal(" ")
                        .append(Text.translatable("tooltip.arqueoutils.amulet.smelt2"))
                        .formatted(Formatting.DARK_GREEN));
            }
            case "forge" -> {
                textConsumer.accept(Text.empty());
                textConsumer.accept(Text.translatable("item.modifiers.offhand")
                        .formatted(Formatting.GRAY));
                textConsumer.accept(Text.literal(" ")
                        .append(Text.translatable("tooltip.arqueoutils.amulet.forge1"))
                        .formatted(Formatting.DARK_GREEN));
                textConsumer.accept(Text.literal(" ")
                        .append(Text.translatable("tooltip.arqueoutils.amulet.forge2"))
                        .formatted(Formatting.DARK_GREEN));
            }
            case "vision" -> {
                textConsumer.accept(Text.empty());
                textConsumer.accept(Text.translatable("item.modifiers.offhand")
                        .formatted(Formatting.GRAY));
                textConsumer.accept(Text.literal(" ")
                        .append(Text.translatable("tooltip.arqueoutils.amulet.vision"))
                        .formatted(Formatting.DARK_GREEN));
            }
            case "potion" -> {
                textConsumer.accept(Text.empty());
                textConsumer.accept(Text.translatable("item.modifiers.offhand")
                        .formatted(Formatting.GRAY));
                textConsumer.accept(Text.literal(" ")
                        .append(Text.translatable("tooltip.arqueoutils.amulet.potion1"))
                        .formatted(Formatting.DARK_GREEN));
                textConsumer.accept(Text.literal(" ")
                        .append(Text.translatable("tooltip.arqueoutils.amulet.potion2"))
                        .formatted(Formatting.DARK_GREEN));
            }
            default -> {}
        }
    }
}

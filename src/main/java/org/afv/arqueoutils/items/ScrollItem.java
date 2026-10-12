package org.afv.arqueoutils.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.afv.arqueoutils.init.ModComponents;
import org.afv.arqueoutils.init.ModItems;
import org.afv.arqueoutils.util.SoundUtils;

import java.util.function.Consumer;

public class ScrollItem extends Item {
    private static final String[] TYPES = new String[]{"armor", "attack_speed", "damage", "health", "toughness", "oxygen", "speed", "experience"};
    public static String[] getTypes() { return TYPES.clone(); }

    public ScrollItem(Settings settings) {
        super(settings);
    }

    public static int getColorByType(String type) {
        int color = 0;
        switch (type) {
            case "damage" -> color = 0xc96363;
            case "attack_speed" -> color = 0xffe896;
            case "armor" -> color = 0xFFFFFF;
            case "toughness" -> color = 0xb388db;
            case "speed" -> color = 0x93fafa;
            case "experience" -> color = 0x81c786;
            case "oxygen" -> color = 0x6a8ceb;
            case "health" -> color = 0xda88db;
            default -> {}
        }
        return color;
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        if (world.isClient()) {
            // skip client-only prediction
            return ActionResult.PASS;
        }

        ItemStack stack = player.getStackInHand(hand);

        if (stack.get(ModComponents.SCROLL_TYPE) == null) {
            ItemStack scroll = new ItemStack(ModItems.SCROLL);
            Random random = player.getRandom();
            String type = TYPES[random.nextInt(TYPES.length)];
            float f = random.nextFloat();
            int quality;

            SoundUtils.sendSoundClient(
                    player, SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA, SoundCategory.MASTER,
                    1, 1.5f
            );

            if (f < 0.05) {
                quality = 4;
                SoundUtils.sendSoundClient(
                        player, SoundEvents.BLOCK_NOTE_BLOCK_CHIME, SoundCategory.MASTER,
                        1, 0.75f
                );
                player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_CHIME.value(), 1, 0.75f);
            }
            else if (f < 0.20) {
                quality = 3;
                SoundUtils.sendSoundClient(
                        player, SoundEvents.BLOCK_NOTE_BLOCK_BELL, SoundCategory.MASTER,
                        1, 1f
                );
            }
            else if (f < 0.50) {
                quality = 2;
                SoundUtils.sendSoundClient(
                        player, SoundEvents.BLOCK_NOTE_BLOCK_BELL, SoundCategory.MASTER,
                        1, 0.75f
                );
            }
            else {
                quality = 1;
            }

            scroll.set(ModComponents.SCROLL_TYPE, type);
            scroll.set(ModComponents.SCROLL_QUALITY, quality);

            if (stack.getCount() == 1) {
                stack.set(ModComponents.SCROLL_TYPE, type);
                stack.set(ModComponents.SCROLL_QUALITY, quality);
            } else {
                player.giveOrDropStack(scroll);
                stack.setCount(stack.getCount() - 1);
            }

            return ActionResult.SUCCESS;
        } else return ActionResult.PASS;
    }

    @Override
    public Text getName(ItemStack stack) {
        Integer quality = stack.get(ModComponents.SCROLL_QUALITY);
        if (quality == null) return super.getName(stack);

        switch (quality) {
            case 1 -> {
                return Text.translatable("item.arqueoutils.upgrade_scroll.common")
                        .formatted(Formatting.GRAY);
            }
            case 2 -> {
                return Text.translatable("item.arqueoutils.upgrade_scroll.rare")
                        .formatted(Formatting.BLUE);
            }
            case 3 -> {
                return Text.translatable("item.arqueoutils.upgrade_scroll.epic")
                        .formatted(Formatting.DARK_PURPLE);
            }
            case 4 -> {
                return Text.translatable("item.arqueoutils.upgrade_scroll.legendary")
                        .formatted(Formatting.GOLD);
            }
            default -> {
                return Text.translatable("item.arqueoutils.upgrade_scroll.unidentified")
                        .formatted(Formatting.WHITE);
            }
        }
    }

    public void addTooltipForScroll(ItemStack stack, Consumer<Text> textConsumer) {
        String type = stack.get(ModComponents.SCROLL_TYPE);
        if (type == null) return;
        Integer quality = stack.get(ModComponents.SCROLL_QUALITY);
        if (quality == null) return;
        String roman = "I";
        switch (quality) {
            case 2 -> roman = "II";
            case 3 -> roman = "III";
            case 4 -> roman = "IV";
            default -> {}
        }
        double d = Math.floor(getValue(type, quality) * 1000) / 1000;
        switch (type) {
            case "damage" -> {
                String s;
                if (d == (int)d) {
                    s = String.valueOf((int)d); // remove .0 for whole numbers
                } else {
                    s = String.valueOf(d);      // keep decimals for fractions
                }

                textConsumer.accept(Text.empty()
                        .append(Text.translatable("tooltip.arqueoutils.scroll.damage"))
                        .append(" ")
                        .append(Text.literal(roman))
                        .withColor(0xc96363));
                textConsumer.accept(Text.empty());
                textConsumer.accept(Text.translatable("item.minecraft.smithing_template.applies_to")
                        .formatted(Formatting.GRAY));
                textConsumer.accept(Text.literal(" ")
                        .append(Text.translatable("tooltip.arqueoutils.scroll.damage.applies_to")
                                .withColor(0xc96363)));
                textConsumer.accept(Text.translatable("tooltip.arqueoutils.scroll.grants")
                        .formatted(Formatting.GRAY));
                textConsumer.accept(Text.literal(" ")
                        .append(Text.literal("+" + s + " "))
                        .append(Text.translatable("tooltip.arqueoutils.scroll.damage.grants"))
                        .withColor(0xc96363));
            }
            case "armor" -> {
                String s;
                if (d == (int)d) {
                    s = String.valueOf((int)d); // remove .0 for whole numbers
                } else {
                    s = String.valueOf(d);      // keep decimals for fractions
                }

                textConsumer.accept(Text.empty()
                        .append(Text.translatable("tooltip.arqueoutils.scroll.armor"))
                        .append(" ")
                        .append(Text.literal(roman))
                        .formatted(Formatting.WHITE));
                textConsumer.accept(Text.empty());
                textConsumer.accept(Text.translatable("item.minecraft.smithing_template.applies_to")
                        .formatted(Formatting.GRAY));
                textConsumer.accept(Text.literal(" ")
                        .append(Text.translatable("tooltip.arqueoutils.scroll.armor.applies_to")
                                .formatted(Formatting.WHITE)));
                textConsumer.accept(Text.translatable("tooltip.arqueoutils.scroll.grants")
                        .formatted(Formatting.GRAY));
                textConsumer.accept(Text.literal(" ")
                        .append(Text.literal("+" + s + " "))
                        .append(Text.translatable("tooltip.arqueoutils.scroll.armor.grants"))
                        .formatted(Formatting.WHITE));
            }
            case "attack_speed" -> {
                String s;
                d *= 100;
                if (d == (int)d) {
                    s = String.valueOf((int)d); // remove .0 for whole numbers
                } else {
                    s = String.valueOf(d);      // keep decimals for fractions
                }

                textConsumer.accept(Text.empty()
                        .append(Text.translatable("tooltip.arqueoutils.scroll.attack_speed"))
                        .append(" ")
                        .append(Text.literal(roman))
                        .withColor(0xffe896));
                textConsumer.accept(Text.empty());
                textConsumer.accept(Text.translatable("item.minecraft.smithing_template.applies_to")
                        .formatted(Formatting.GRAY));
                textConsumer.accept(Text.literal(" ")
                        .append(Text.translatable("tooltip.arqueoutils.scroll.attack_speed.applies_to")
                                .withColor(0xffe896)));
                textConsumer.accept(Text.translatable("tooltip.arqueoutils.scroll.grants")
                        .formatted(Formatting.GRAY));
                textConsumer.accept(Text.literal(" ")
                        .append(Text.literal("+" + s + "% "))
                        .append(Text.translatable("tooltip.arqueoutils.scroll.attack_speed.grants"))
                            .withColor(0xffe896));
            }
            case "speed" -> {
                d *= 100;
                String s;
                if (d == (int)d) {
                    s = String.valueOf((int)d); // remove .0 for whole numbers
                } else {
                    s = String.valueOf(d);      // keep decimals for fractions
                }

                textConsumer.accept(Text.empty()
                        .append(Text.translatable("tooltip.arqueoutils.scroll.speed"))
                        .append(" ")
                        .append(Text.literal(roman))
                        .withColor(0x93fafa));
                textConsumer.accept(Text.empty());
                textConsumer.accept(Text.translatable("item.minecraft.smithing_template.applies_to")
                        .formatted(Formatting.GRAY));
                textConsumer.accept(Text.literal(" ")
                        .append(Text.translatable("tooltip.arqueoutils.scroll.speed.applies_to")
                                .withColor(0x93fafa)));
                textConsumer.accept(Text.translatable("tooltip.arqueoutils.scroll.grants")
                        .formatted(Formatting.GRAY));
                textConsumer.accept(Text.literal(" ")
                        .append(Text.literal("+" + s + "% "))
                        .append(Text.translatable("tooltip.arqueoutils.scroll.speed.grants"))
                        .withColor(0x93fafa));
            }
            case "toughness" -> {
                String s;
                if (d == (int)d) {
                    s = String.valueOf((int)d); // remove .0 for whole numbers
                } else {
                    s = String.valueOf(d);      // keep decimals for fractions
                }

                textConsumer.accept(Text.empty()
                        .append(Text.translatable("tooltip.arqueoutils.scroll.toughness"))
                        .append(" ")
                        .append(Text.literal(roman))
                        .withColor(0xb388db));
                textConsumer.accept(Text.empty());
                textConsumer.accept(Text.translatable("item.minecraft.smithing_template.applies_to")
                        .formatted(Formatting.GRAY));
                textConsumer.accept(Text.literal(" ")
                        .append(Text.translatable("tooltip.arqueoutils.scroll.toughness.applies_to")
                                .withColor(0xb388db)));
                textConsumer.accept(Text.translatable("tooltip.arqueoutils.scroll.grants")
                        .formatted(Formatting.GRAY));
                textConsumer.accept(Text.literal(" ")
                        .append(Text.literal("+" + s + " "))
                        .append(Text.translatable("tooltip.arqueoutils.scroll.toughness.grants"))
                        .withColor(0xb388db));
            }
            case "oxygen" -> {
                String s;
                if (d == (int)d) {
                    s = String.valueOf((int)d); // remove .0 for whole numbers
                } else {
                    s = String.valueOf(d);      // keep decimals for fractions
                }

                textConsumer.accept(Text.empty()
                        .append(Text.translatable("tooltip.arqueoutils.scroll.oxygen"))
                        .append(" ")
                        .append(Text.literal(roman))
                        .withColor(0x6a8ceb));
                textConsumer.accept(Text.empty());
                textConsumer.accept(Text.translatable("item.minecraft.smithing_template.applies_to")
                        .formatted(Formatting.GRAY));
                textConsumer.accept(Text.literal(" ")
                        .append(Text.translatable("tooltip.arqueoutils.scroll.oxygen.applies_to")
                                .withColor(0x6a8ceb)));
                textConsumer.accept(Text.translatable("tooltip.arqueoutils.scroll.grants")
                        .formatted(Formatting.GRAY));
                textConsumer.accept(Text.literal(" ")
                        .append(Text.literal("+" + s + " "))
                        .append(Text.translatable("tooltip.arqueoutils.scroll.oxygen.grants"))
                        .withColor(0x6a8ceb));
            }
            case "health" -> {
                String s;
                if (d == (int)d) {
                    s = String.valueOf((int)d); // remove .0 for whole numbers
                } else {
                    s = String.valueOf(d);      // keep decimals for fractions
                }

                textConsumer.accept(Text.empty()
                        .append(Text.translatable("tooltip.arqueoutils.scroll.health"))
                        .append(" ")
                        .append(Text.literal(roman))
                        .withColor(0xda88db));
                textConsumer.accept(Text.empty());
                textConsumer.accept(Text.translatable("item.minecraft.smithing_template.applies_to")
                        .formatted(Formatting.GRAY));
                textConsumer.accept(Text.literal(" ")
                        .append(Text.translatable("tooltip.arqueoutils.scroll.health.applies_to")
                                .withColor(0xda88db)));
                textConsumer.accept(Text.translatable("tooltip.arqueoutils.scroll.grants")
                        .formatted(Formatting.GRAY));
                textConsumer.accept(Text.literal(" ")
                        .append(Text.literal("+" + s + " "))
                        .append(Text.translatable("tooltip.arqueoutils.scroll.health.grants"))
                        .withColor(0xda88db));
            }
            case "experience" -> {
                d *= 100;
                String s;
                if (d == (int)d) {
                    s = String.valueOf((int)d); // remove .0 for whole numbers
                } else {
                    s = String.valueOf(d);      // keep decimals for fractions
                }

                textConsumer.accept(Text.empty()
                        .append(Text.translatable("tooltip.arqueoutils.scroll.experience"))
                        .append(" ")
                        .append(Text.literal(roman))
                        .withColor(0x81c786));
                textConsumer.accept(Text.empty());
                textConsumer.accept(Text.translatable("item.minecraft.smithing_template.applies_to")
                        .formatted(Formatting.GRAY));
                textConsumer.accept(Text.literal(" ")
                        .append(Text.translatable("tooltip.arqueoutils.scroll.experience.applies_to")
                                .withColor(0x81c786)));
                textConsumer.accept(Text.translatable("tooltip.arqueoutils.scroll.grants")
                        .formatted(Formatting.GRAY));
                textConsumer.accept(Text.literal(" ")
                        .append(Text.literal("+" + s + "% "))
                        .append(Text.translatable("tooltip.arqueoutils.scroll.experience.grants"))
                        .withColor(0x81c786));
            }
            default -> {}
        }
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        Integer quality = stack.get(ModComponents.SCROLL_QUALITY);
        // Only scrolls of quality 4 have the "glint"
        return quality != null && quality == 4;
    }

    public static double getValue(String upgrade_type, int upgrade_quality) {
        double boost = 0;
        switch (upgrade_type) {
            case "damage", "oxygen", "armor" -> {
                boost = 0.25 * upgrade_quality;
            }
            case "toughness", "health" -> {
                boost = 0.5 * upgrade_quality;
            }
            case "speed", "attack_speed", "experience" -> {
                boost = 0.025 * upgrade_quality;
            }
            default -> {}
        }
        return boost;
    }
}

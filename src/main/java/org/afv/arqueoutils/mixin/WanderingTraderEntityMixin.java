package org.afv.arqueoutils.mixin;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.TradedItem;
import org.afv.arqueoutils.init.ModItems;
import org.afv.arqueoutils.util.EnchHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(WanderingTraderEntity.class)
public abstract class WanderingTraderEntityMixin {

    @Inject(method = "fillRecipes", at = @At("TAIL"))
    private void arqueoutils$addGuaranteedTrades(CallbackInfo ci) {
        WanderingTraderEntity trader = (WanderingTraderEntity)(Object)this;
        Random random = trader.getRandom();

        TradeOfferList offers = trader.getOffers();

        // Optional: prevent duplicate insertion
        if (containsOurTrade(offers)) {
            return;
        }

        int cost1 = (int) (16 + Math.floor(9 * Math.random()));
        ItemStack result1 = new ItemStack(ModItems.SCROLL);
        TradeOffer offer1 = new TradeOffer(
                new TradedItem(Items.EMERALD, cost1), // cost
                Optional.of(new TradedItem(Items.PAPER, 10)),
                result1,                           // result
                3,                                // max uses
                5,                                // experience
                0.05f                             // price multiplier
        );
        addAtIndex(offers, offer1, 2);

        if (random.nextFloat() < 0.025) {
            int cost2 = (int) (32 + Math.floor(33 * Math.random()));
            TradeOffer offer2 = new TradeOffer(
                    new TradedItem(Items.EMERALD, cost2), // cost
                    Optional.of(new TradedItem(Items.GOLDEN_APPLE, 1)),
                    new ItemStack(Items.ENCHANTED_GOLDEN_APPLE),                           // result
                    1,                                // max uses
                    20,                                // experience
                    0.05f                             // price multiplier
            );
            addAtIndex(offers, offer2, 3);
        } else if (random.nextFloat() < 0.15) {
            int cost2 = (int) (8 + Math.floor(9 * Math.random()));

            ItemStack result2 = new ItemStack(Items.ENCHANTED_BOOK);

            ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);

            builder.add(EnchHelper.getEntry(Enchantments.MENDING, trader.getEntityWorld()), 1);

            result2.set(DataComponentTypes.STORED_ENCHANTMENTS, builder.build());

            TradeOffer offer2 = new TradeOffer(
                    new TradedItem(Items.EMERALD, cost2), // cost
                    Optional.of(new TradedItem(Items.BOOK, 1)),
                    result2,                           // result
                    1,                                // max uses
                    20,                                // experience
                    0.05f                             // price multiplier
            );
            addAtIndex(offers, offer2, 3);
        } else if (random.nextFloat() < 0.2) {
            int cost2 = (int) (32 + Math.floor(33 * Math.random()));
            ItemStack result2 = new ItemStack(new Item[]{
                    Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE,
                    Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE,
                    Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE,
                    Items.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE}[random.nextInt(4)]
            );
            TradeOffer offer2 = new TradeOffer(
                    new TradedItem(Items.EMERALD, cost2), // cost
                    result2,                         // result
                    1,                                // max uses
                    20,                                // experience
                    0.05f                             // price multiplier
            );
            addAtIndex(offers, offer2, 3);
        }

        if (random.nextFloat() < 0.4) {
            int cost2 = (int) (4 + Math.floor(8 * Math.random()));
            ItemStack result2 = new ItemStack(new Item[]{
                    Items.BLUE_BUNDLE,
                    Items.BLACK_BUNDLE,
                    Items.YELLOW_BUNDLE,
                    Items.RED_BUNDLE}[random.nextInt(4)]
            );
            TradeOffer offer2 = new TradeOffer(
                    new TradedItem(Items.EMERALD, cost2), // cost
                    result2,                           // result
                    3,                                // max uses
                    5,                                // experience
                    0.05f                             // price multiplier
            );
            addAtIndex(offers, offer2, 3);
        }

        int cost3 = (int) (12 + Math.floor(12 * Math.random()));
        ItemStack result2 = new ItemStack(new Item[]{
                ModItems.MEGALOVANIA_MUSIC_DISC,
                ModItems.DANCE_OF_THE_KNIGHTS_MUSIC_DISC,
                ModItems.PAINT_IT_BLACK_MUSIC_DISC,
                ModItems.GOURMET_RACE_MUSIC_DISC,
                ModItems.GERUDO_VALLEY_MUSIC_DISC,
                ModItems.PORTAL_RADIO_MUSIC_DISC,
                ModItems.STILL_ALIVE_MUSIC_DISC,
                ModItems.GLORY_ARSTOTSZKA_MUSIC_DISC,
                ModItems.TETRIS_A_MUSIC_DISC,
                ModItems.DARK_NIGHT_MUSIC_DISC,
                ModItems.ROARING_KNIGHT_MUSIC_DISC,
                ModItems.MANTIS_LORDS_MUSIC_DISC,
                ModItems.COGWORK_DANCERS_MUSIC_DISC,
                ModItems.MALENIA_MUSIC_DISC,
                ModItems.CRYPTEQUE_MUSIC_DISC,
                ModItems.BALATRO_MUSIC_DISC}[random.nextInt(16)]
        );
        TradeOffer offer2 = new TradeOffer(
                new TradedItem(Items.EMERALD, cost3), // cost
                result2,                           // result
                1,                                // max uses
                5,                                // experience
                0.05f                             // price multiplier
        );
        addAtIndex(offers, offer2, 3);
    }

    @Unique
    private void addAtIndex(TradeOfferList offers, TradeOffer offer, int ind) {
        offers.add(offers.getLast());
        for (int i = offers.size() - 1; i > ind; i--) {
            offers.set(i, offers.get(i - 1));
        }
        offers.set(ind, offer);
    }

    @Unique
    private boolean containsOurTrade(TradeOfferList offers) {
        for (TradeOffer offer : offers) {
            if (offer.getSellItem().isOf(ModItems.SCROLL)) {
                return true;
            }
        }
        return false;
    }
}
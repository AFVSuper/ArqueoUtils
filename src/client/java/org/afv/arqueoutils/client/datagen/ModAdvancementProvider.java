package org.afv.arqueoutils.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.predicate.component.ComponentsPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;
import org.afv.arqueoutils.criterion.ApplyScrollCriterion;
import org.afv.arqueoutils.criterion.InvisFrameCriterion;
import org.afv.arqueoutils.init.ModComponents;
import org.afv.arqueoutils.init.ModCriteria;
import org.afv.arqueoutils.init.ModItems;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModAdvancementProvider extends FabricAdvancementProvider {
    public ModAdvancementProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(RegistryWrapper.@NotNull WrapperLookup wrapperLookup, @NotNull Consumer<AdvancementEntry> consumer) {
        AdvancementEntry arqueoRoot = Advancement.Builder.create()
                .display(
                        ModItems.ARCHAIC_DIAMOND,
                        Text.translatable("advancements.arqueoutils.arqueo_root"),
                        Text.translatable("advancements.arqueoutils.arqueo_root.desc"),
                        Identifier.ofVanilla("block/cobbled_deepslate"),
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false)
                .criterion("get_online", TickCriterion.Conditions.createTick())
                .build(consumer, ArqueoUtils.ModID + ":root");

        AdvancementEntry getLuminisCore = Advancement.Builder.create()
                .parent(arqueoRoot)
                .display(
                        ModItems.LUMINIS_CORE,
                        Text.translatable("advancements.arqueoutils.luminis_core"),
                        Text.translatable("advancements.arqueoutils.luminis_core.desc"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false)
                .criterion("get_luminis_core", InventoryChangedCriterion.Conditions.items(ModItems.LUMINIS_CORE))
                .build(consumer, ArqueoUtils.ModID + ":luminis_core_adv");

        AdvancementEntry getFullLuminisArmor = Advancement.Builder.create()
                .parent(getLuminisCore)
                .display(
                        ModItems.LUMINIS_CHESTPLATE,
                        Text.translatable("advancements.arqueoutils.full_luminis"),
                        Text.translatable("advancements.arqueoutils.full_luminis.desc"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false)
                // Helmet
                .criterion("helmet",
                        InventoryChangedCriterion.Conditions.items(ModItems.LUMINIS_HELMET)
                )
                // Chestplate
                .criterion("chestplate",
                        InventoryChangedCriterion.Conditions.items(ModItems.LUMINIS_CHESTPLATE)
                )
                // Leggings
                .criterion("leggings",
                        InventoryChangedCriterion.Conditions.items(ModItems.LUMINIS_LEGGINGS)
                )
                // Boots
                .criterion("boots",
                        InventoryChangedCriterion.Conditions.items(ModItems.LUMINIS_BOOTS)
                )
                .build(consumer, ArqueoUtils.ModID + ":luminis_armor");

        ItemStack fullLuminisDyedIcon = new ItemStack(ModItems.LUMINIS_CHESTPLATE);

        fullLuminisDyedIcon.set(
                DataComponentTypes.DYED_COLOR,
                new DyedColorComponent(0x55B6E8) // light blue
        );

        AdvancementEntry getFullLuminisDyedArmor = Advancement.Builder.create()
                .parent(getFullLuminisArmor)
                .display(
                        fullLuminisDyedIcon,
                        Text.translatable("advancements.arqueoutils.full_dyed_luminis"),
                        Text.translatable("advancements.arqueoutils.full_dyed_luminis.desc"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true,
                        true,
                        true)
                // Helmet
                .criterion("helmet",
                        InventoryChangedCriterion.Conditions.items(
                                ItemPredicate.Builder.create()
                                        .items(wrapperLookup.getOrThrow(RegistryKeys.ITEM), ModItems.LUMINIS_HELMET)
                                        .components(
                                                ComponentsPredicate.Builder.create()
                                                        .has(DataComponentTypes.DYED_COLOR)
                                                        .build()
                                        )
                        )
                )
                // Chestplate
                .criterion("chestplate",
                        InventoryChangedCriterion.Conditions.items(
                                ItemPredicate.Builder.create()
                                        .items(wrapperLookup.getOrThrow(RegistryKeys.ITEM), ModItems.LUMINIS_CHESTPLATE)
                                        .components(
                                                ComponentsPredicate.Builder.create()
                                                        .has(DataComponentTypes.DYED_COLOR)
                                                        .build()
                                        )
                        )
                )
                // Leggings
                .criterion("leggings",
                        InventoryChangedCriterion.Conditions.items(
                                ItemPredicate.Builder.create()
                                        .items(wrapperLookup.getOrThrow(RegistryKeys.ITEM), ModItems.LUMINIS_LEGGINGS)
                                        .components(
                                                ComponentsPredicate.Builder.create()
                                                        .has(DataComponentTypes.DYED_COLOR)
                                                        .build()
                                        )
                        )
                )
                // Boots
                .criterion("boots",
                        InventoryChangedCriterion.Conditions.items(
                                ItemPredicate.Builder.create()
                                        .items(wrapperLookup.getOrThrow(RegistryKeys.ITEM), ModItems.LUMINIS_BOOTS)
                                        .components(
                                                ComponentsPredicate.Builder.create()
                                                        .has(DataComponentTypes.DYED_COLOR)
                                                        .build()
                                        )
                        )
                )
                .build(consumer, ArqueoUtils.ModID + ":luminis_dyed");

        AdvancementEntry getInvisInk = Advancement.Builder.create()
                .parent(arqueoRoot)
                .display(
                        ModItems.INVISIBLE_INK,
                        Text.translatable("advancements.arqueoutils.invis_ink"),
                        Text.translatable("advancements.arqueoutils.invis_ink.desc"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false)
                .criterion("get_invis_ink", InventoryChangedCriterion.Conditions.items(ModItems.INVISIBLE_INK))
                .build(consumer, ArqueoUtils.ModID + ":invis_ink");

        AdvancementEntry getInvisArmor = Advancement.Builder.create()
                .parent(getInvisInk)
                .display(
                        Items.CHAINMAIL_CHESTPLATE,
                        Text.translatable("advancements.arqueoutils.invis_armor"),
                        Text.translatable("advancements.arqueoutils.invis_armor.desc"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false)
                .criterion("invis_armor",
                        InventoryChangedCriterion.Conditions.items(
                                ItemPredicate.Builder.create()
                                        .tag(wrapperLookup.getOrThrow(RegistryKeys.ITEM), ItemTags.TRIMMABLE_ARMOR)
                                        .components(
                                                ComponentsPredicate.Builder.create()
                                                        .has(ModComponents.INVISIBLE_DYED)
                                                        .build()
                                        )
                        )
                )
                .build(consumer, ArqueoUtils.ModID + ":invis_armor");

        AdvancementEntry makeInvisFrame = Advancement.Builder.create()
                .parent(getInvisInk)
                .display(
                        Items.ITEM_FRAME,
                        Text.translatable("advancements.arqueoutils.invis_frame"),
                        Text.translatable("advancements.arqueoutils.invis_frame.desc"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false)
                .criterion("invis_frame",
                        ModCriteria.INVIS_FRAME.create(new InvisFrameCriterion.Conditions(Optional.empty()))
                )
                .build(consumer, ArqueoUtils.ModID + ":invis_frame");

        AdvancementEntry getScroll = Advancement.Builder.create()
                .parent(arqueoRoot)
                .display(
                        ModItems.SCROLL,
                        Text.translatable("advancements.arqueoutils.get_scroll"),
                        Text.translatable("advancements.arqueoutils.get_scroll.desc"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false)
                .criterion("get_scroll",
                        InventoryChangedCriterion.Conditions.items(
                                ModItems.SCROLL
                        )
                )
                .build(consumer, ArqueoUtils.ModID + ":get_scroll");

        ItemStack scrollIcon = new ItemStack(ModItems.SCROLL);
        scrollIcon.set(ModComponents.SCROLL_TYPE, "speed");

        AdvancementEntry useScroll = Advancement.Builder.create()
                .parent(getScroll)
                .display(
                        scrollIcon,
                        Text.translatable("advancements.arqueoutils.use_scroll"),
                        Text.translatable("advancements.arqueoutils.use_scroll.desc"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false)
                .criterion("use_scroll",
                        ModCriteria.APPLY_SCROLL.create(new ApplyScrollCriterion.Conditions(
                                Optional.empty(),
                                1
                        ))
                )
                .build(consumer, ArqueoUtils.ModID + ":use_scroll");

        ItemStack legScrollIcon = new ItemStack(ModItems.SCROLL);
        legScrollIcon.set(ModComponents.SCROLL_TYPE, "damage");
        legScrollIcon.set(ModComponents.SCROLL_QUALITY, 4);

        AdvancementEntry useLegendaryScroll = Advancement.Builder.create()
                .parent(useScroll)
                .display(
                        legScrollIcon,
                        Text.translatable("advancements.arqueoutils.legendary_scroll"),
                        Text.translatable("advancements.arqueoutils.legendary_scroll.desc"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true,
                        true,
                        true)
                .criterion("legendary_scroll",
                        ModCriteria.APPLY_SCROLL.create(new ApplyScrollCriterion.Conditions(
                                Optional.empty(),
                                4
                        ))
                )
                .build(consumer, ArqueoUtils.ModID + ":legendary_scroll");

        AdvancementEntry getCustomDisc = Advancement.Builder.create()
                .parent(arqueoRoot)
                .display(
                        ModItems.ROARING_KNIGHT_MUSIC_DISC,
                        Text.translatable("advancements.arqueoutils.custom_disc"),
                        Text.translatable("advancements.arqueoutils.custom_disc.desc"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false)
                .criterion("megalovania",
                        InventoryChangedCriterion.Conditions.items(ModItems.MEGALOVANIA_MUSIC_DISC)
                )
                .criterion("dance_of_the_knights",
                        InventoryChangedCriterion.Conditions.items(ModItems.DANCE_OF_THE_KNIGHTS_MUSIC_DISC)
                )
                .criterion("portal_radio",
                        InventoryChangedCriterion.Conditions.items(ModItems.PORTAL_RADIO_MUSIC_DISC)
                )
                .criterion("still_alive",
                        InventoryChangedCriterion.Conditions.items(ModItems.STILL_ALIVE_MUSIC_DISC)
                )
                .criterion("balatro",
                        InventoryChangedCriterion.Conditions.items(ModItems.BALATRO_MUSIC_DISC)
                )
                .criterion("malenia",
                        InventoryChangedCriterion.Conditions.items(ModItems.MALENIA_MUSIC_DISC)
                )
                .criterion("tetris",
                        InventoryChangedCriterion.Conditions.items(ModItems.TETRIS_A_MUSIC_DISC)
                )
                .criterion("cogwork_dancers",
                        InventoryChangedCriterion.Conditions.items(ModItems.COGWORK_DANCERS_MUSIC_DISC)
                )
                .criterion("crypteque",
                        InventoryChangedCriterion.Conditions.items(ModItems.CRYPTEQUE_MUSIC_DISC)
                )
                .criterion("dark_night",
                        InventoryChangedCriterion.Conditions.items(ModItems.DARK_NIGHT_MUSIC_DISC)
                )
                .criterion("roaring_knight",
                        InventoryChangedCriterion.Conditions.items(ModItems.ROARING_KNIGHT_MUSIC_DISC)
                )
                .criterion("gourmet_race",
                        InventoryChangedCriterion.Conditions.items(ModItems.GOURMET_RACE_MUSIC_DISC)
                )
                .criterion("papers_please",
                        InventoryChangedCriterion.Conditions.items(ModItems.GLORY_ARSTOTSZKA_MUSIC_DISC)
                )
                .criterion("gerudo_valley",
                        InventoryChangedCriterion.Conditions.items(ModItems.GERUDO_VALLEY_MUSIC_DISC)
                )
                .criteriaMerger(AdvancementRequirements.CriterionMerger.OR)
                .build(consumer, ArqueoUtils.ModID + ":custom_disc");

        AdvancementEntry getAllCustomDiscs = Advancement.Builder.create()
                .parent(getCustomDisc)
                .display(
                        ModItems.PAINT_IT_BLACK_MUSIC_DISC,
                        Text.translatable("advancements.arqueoutils.all_discs"),
                        Text.translatable("advancements.arqueoutils.all_discs.desc"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true,
                        true,
                        true)
                .criterion("megalovania",
                        InventoryChangedCriterion.Conditions.items(ModItems.MEGALOVANIA_MUSIC_DISC)
                )
                .criterion("dance_of_the_knights",
                        InventoryChangedCriterion.Conditions.items(ModItems.DANCE_OF_THE_KNIGHTS_MUSIC_DISC)
                )
                .criterion("portal_radio",
                        InventoryChangedCriterion.Conditions.items(ModItems.PORTAL_RADIO_MUSIC_DISC)
                )
                .criterion("still_alive",
                        InventoryChangedCriterion.Conditions.items(ModItems.STILL_ALIVE_MUSIC_DISC)
                )
                .criterion("balatro",
                        InventoryChangedCriterion.Conditions.items(ModItems.BALATRO_MUSIC_DISC)
                )
                .criterion("malenia",
                        InventoryChangedCriterion.Conditions.items(ModItems.MALENIA_MUSIC_DISC)
                )
                .criterion("tetris",
                        InventoryChangedCriterion.Conditions.items(ModItems.TETRIS_A_MUSIC_DISC)
                )
                .criterion("cogwork_dancers",
                        InventoryChangedCriterion.Conditions.items(ModItems.COGWORK_DANCERS_MUSIC_DISC)
                )
                .criterion("crypteque",
                        InventoryChangedCriterion.Conditions.items(ModItems.CRYPTEQUE_MUSIC_DISC)
                )
                .criterion("dark_night",
                        InventoryChangedCriterion.Conditions.items(ModItems.DARK_NIGHT_MUSIC_DISC)
                )
                .criterion("roaring_knight",
                        InventoryChangedCriterion.Conditions.items(ModItems.ROARING_KNIGHT_MUSIC_DISC)
                )
                .criterion("gourmet_race",
                        InventoryChangedCriterion.Conditions.items(ModItems.GOURMET_RACE_MUSIC_DISC)
                )
                .criterion("papers_please",
                        InventoryChangedCriterion.Conditions.items(ModItems.GLORY_ARSTOTSZKA_MUSIC_DISC)
                )
                .criterion("gerudo_valley",
                        InventoryChangedCriterion.Conditions.items(ModItems.GERUDO_VALLEY_MUSIC_DISC)
                )
                .build(consumer, ArqueoUtils.ModID + ":all_discs");

        AdvancementEntry getTrimRemover = Advancement.Builder.create()
                .parent(arqueoRoot)
                .display(
                        ModItems.EMPTY_TRIM,
                        Text.translatable("advancements.arqueoutils.trim_remover"),
                        Text.translatable("advancements.arqueoutils.trim_remover.desc"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false)
                .criterion("empty_trim",
                        InventoryChangedCriterion.Conditions.items(ModItems.EMPTY_TRIM)
                )
                .build(consumer, ArqueoUtils.ModID + ":trim_remover");

        AdvancementEntry getModelCard = Advancement.Builder.create()
                .parent(arqueoRoot)
                .display(
                        ModItems.MODEL_CARD,
                        Text.translatable("advancements.arqueoutils.model_card"),
                        Text.translatable("advancements.arqueoutils.model_card.desc"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false)
                .criterion("model_card",
                        InventoryChangedCriterion.Conditions.items(ModItems.MODEL_CARD)
                )
                .build(consumer, ArqueoUtils.ModID + ":model_card");

        AdvancementEntry getEmptyAmulet = Advancement.Builder.create()
                .parent(arqueoRoot)
                .display(
                        ModItems.EMPTY_AMULET,
                        Text.translatable("advancements.arqueoutils.empty_amulet"),
                        Text.translatable("advancements.arqueoutils.empty_amulet.desc"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false)
                .criterion("empty_amulet",
                        InventoryChangedCriterion.Conditions.items(ModItems.EMPTY_AMULET)
                )
                .build(consumer, ArqueoUtils.ModID + ":empty_amulet");

        AdvancementEntry getBlazingHeart = Advancement.Builder.create()
                .parent(getEmptyAmulet)
                .display(
                        ModItems.BLAZING_HEART,
                        Text.translatable("advancements.arqueoutils.blazing_heart"),
                        Text.translatable("advancements.arqueoutils.blazing_heart.desc"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false)
                .criterion("blazing_heart",
                        InventoryChangedCriterion.Conditions.items(ModItems.BLAZING_HEART)
                )
                .build(consumer, ArqueoUtils.ModID + ":blazing_heart");

        AdvancementEntry getBastionTreasure = Advancement.Builder.create()
                .parent(getEmptyAmulet)
                .display(
                        ModItems.BASTION_TREASURE,
                        Text.translatable("advancements.arqueoutils.bastion_treasure"),
                        Text.translatable("advancements.arqueoutils.bastion_treasure.desc"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false)
                .criterion("bastion_treasure",
                        InventoryChangedCriterion.Conditions.items(ModItems.BASTION_TREASURE)
                )
                .build(consumer, ArqueoUtils.ModID + ":bastion_treasure");

        AdvancementEntry getElvenSilver = Advancement.Builder.create()
                .parent(getEmptyAmulet)
                .display(
                        ModItems.SILVER_PLATES,
                        Text.translatable("advancements.arqueoutils.elven_silver"),
                        Text.translatable("advancements.arqueoutils.elven_silver.desc"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false)
                .criterion("elven_silver",
                        InventoryChangedCriterion.Conditions.items(ModItems.SILVER_PLATES)
                )
                .build(consumer, ArqueoUtils.ModID + ":elven_silver");

        AdvancementEntry getAlchemicalCodex = Advancement.Builder.create()
                .parent(getEmptyAmulet)
                .display(
                        ModItems.ALCHEMIST_TOME,
                        Text.translatable("advancements.arqueoutils.alchemical_codex"),
                        Text.translatable("advancements.arqueoutils.alchemical_codex.desc"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false)
                .criterion("alchemical_codex",
                        InventoryChangedCriterion.Conditions.items(ModItems.ALCHEMIST_TOME)
                )
                .build(consumer, ArqueoUtils.ModID + ":alchemical_codex");

        AdvancementEntry getPerfectJade = Advancement.Builder.create()
                .parent(getEmptyAmulet)
                .display(
                        ModItems.PERFECT_JADE,
                        Text.translatable("advancements.arqueoutils.perfect_jade"),
                        Text.translatable("advancements.arqueoutils.perfect_jade.desc"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false)
                .criterion("perfect_jade",
                        InventoryChangedCriterion.Conditions.items(ModItems.PERFECT_JADE)
                )
                .build(consumer, ArqueoUtils.ModID + ":perfect_jade");

        AdvancementEntry getSmeltAmulet = Advancement.Builder.create()
                .parent(getBlazingHeart)
                .display(
                        ModItems.SMELT_AMULET,
                        Text.translatable("advancements.arqueoutils.smelt_amulet"),
                        Text.translatable("advancements.arqueoutils.smelt_amulet.desc"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false)
                .criterion("smelt_amulet",
                        InventoryChangedCriterion.Conditions.items(ModItems.SMELT_AMULET)
                )
                .build(consumer, ArqueoUtils.ModID + ":smelt_amulet");

        AdvancementEntry getLootingAmulet = Advancement.Builder.create()
                .parent(getBastionTreasure)
                .display(
                        ModItems.LOOTING_AMULET,
                        Text.translatable("advancements.arqueoutils.looting_amulet"),
                        Text.translatable("advancements.arqueoutils.looting_amulet.desc"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false)
                .criterion("looting_amulet",
                        InventoryChangedCriterion.Conditions.items(ModItems.LOOTING_AMULET)
                )
                .build(consumer, ArqueoUtils.ModID + ":looting_amulet");

        AdvancementEntry getForgeAmulet = Advancement.Builder.create()
                .parent(getElvenSilver)
                .display(
                        ModItems.FORGE_AMULET,
                        Text.translatable("advancements.arqueoutils.forge_amulet"),
                        Text.translatable("advancements.arqueoutils.forge_amulet.desc"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false)
                .criterion("forge_amulet",
                        InventoryChangedCriterion.Conditions.items(ModItems.FORGE_AMULET)
                )
                .build(consumer, ArqueoUtils.ModID + ":forge_amulet");

        AdvancementEntry getAlchemistAmulet = Advancement.Builder.create()
                .parent(getAlchemicalCodex)
                .display(
                        ModItems.POTION_AMULET,
                        Text.translatable("advancements.arqueoutils.alchemist_amulet"),
                        Text.translatable("advancements.arqueoutils.alchemist_amulet.desc"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false)
                .criterion("alchemist_amulet",
                        InventoryChangedCriterion.Conditions.items(ModItems.POTION_AMULET)
                )
                .build(consumer, ArqueoUtils.ModID + ":alchemist_amulet");

        AdvancementEntry getFortuneAmulet = Advancement.Builder.create()
                .parent(getPerfectJade)
                .display(
                        ModItems.FORTUNE_AMULET,
                        Text.translatable("advancements.arqueoutils.fortune_amulet"),
                        Text.translatable("advancements.arqueoutils.fortune_amulet.desc"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false)
                .criterion("fortune_amulet",
                        InventoryChangedCriterion.Conditions.items(ModItems.FORTUNE_AMULET)
                )
                .build(consumer, ArqueoUtils.ModID + ":fortune_amulet");

        AdvancementEntry getWindRelic = Advancement.Builder.create()
                .parent(arqueoRoot)
                .display(
                        ModItems.WIND_RELIC,
                        Text.translatable("advancements.arqueoutils.wind_relic"),
                        Text.translatable("advancements.arqueoutils.wind_relic.desc"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false)
                .criterion("wind_relic",
                        InventoryChangedCriterion.Conditions.items(ModItems.WIND_RELIC)
                )
                .build(consumer, ArqueoUtils.ModID + ":wind_relic");

        AdvancementEntry getWindTrident = Advancement.Builder.create()
                .parent(getWindRelic)
                .display(
                        ModItems.WIND_TRIDENT,
                        Text.translatable("advancements.arqueoutils.wind_trident"),
                        Text.translatable("advancements.arqueoutils.wind_trident.desc"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false)
                .criterion("wind_trident",
                        InventoryChangedCriterion.Conditions.items(ModItems.WIND_TRIDENT)
                )
                .build(consumer, ArqueoUtils.ModID + ":wind_trident");

        AdvancementEntry getMagnet = Advancement.Builder.create()
                .parent(arqueoRoot)
                .display(
                        ModItems.MAGNET,
                        Text.translatable("advancements.arqueoutils.magnet"),
                        Text.translatable("advancements.arqueoutils.magnet.desc"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false)
                .criterion("magnet",
                        InventoryChangedCriterion.Conditions.items(ModItems.MAGNET)
                )
                .build(consumer, ArqueoUtils.ModID + ":magnet");
    }
}

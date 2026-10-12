package org.afv.arqueoutils.init;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;
import org.afv.arqueoutils.items.OrePowderItem;
import org.afv.arqueoutils.items.ScrollItem;

public final class ModItemGroups {

    public static final RegistryKey<ItemGroup> ARQUEO_UTILS_TAB =
            RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.of(ArqueoUtils.ModID, "arqueoutils1"));

    public static final RegistryKey<ItemGroup> SCROLLS_TAB =
            RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.of(ArqueoUtils.ModID, "arqueoutils2-scrolls"));

    public static final RegistryKey<ItemGroup> MUSIC_DISCS_TAB =
            RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.of(ArqueoUtils.ModID, "arqueoutils3-arqueo_discs"));

    public static final RegistryKey<ItemGroup> ORE_POWDER_TAB =
            RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.of(ArqueoUtils.ModID, "arqueoutils4-ore_powder"));

    public static void init() {
        Registry.register(
                Registries.ITEM_GROUP,
                ARQUEO_UTILS_TAB.getValue(),
                FabricItemGroup.builder()
                        .displayName(Text.translatable("itemGroup.arqueoutils"))
                        .icon(() -> new ItemStack(ModItems.ARCHAIC_DIAMOND))
                        .entries((context, entries) -> {
                            entries.add(ModItems.MAGNET);
                            entries.add(ModItems.VOID_TOTEM);
                            entries.add(ModItems.ARCHAIC_DIAMOND);
                            entries.add(ModItems.PERFECT_JADE);
                            entries.add(ModItems.BASTION_TREASURE);
                            entries.add(ModItems.BLAZING_HEART);
                            entries.add(ModItems.ALCHEMIST_TOME);
                            entries.add(ModItems.SILVER_PLATES);
                            entries.add(ModItems.EMPTY_AMULET);
                            entries.add(ModItems.FORTUNE_AMULET);
                            entries.add(ModItems.LOOTING_AMULET);
                            entries.add(ModItems.SMELT_AMULET);
                            entries.add(ModItems.POTION_AMULET);
                            entries.add(ModItems.FORGE_AMULET);
                            entries.add(ModItems.EMPTY_TRIM);
                            entries.add(ModItems.INVISIBLE_INK);
                            entries.add(ModItems.MODEL_CARD);
                            entries.add(ModItems.WIND_RELIC);
                            entries.add(ModItems.WIND_TRIDENT);
//                            entries.add(ModItems.DARK_SPEAR);
                            entries.add(ModBlocks.ELYTRA_DISPENSER);
//                            entries.add(ModItems.DEATH_BOOK);
                            entries.add(ModItems.LUMINIS_CORE);
                            entries.add(ModItems.LUMINIS_HELMET);
                            entries.add(ModItems.LUMINIS_CHESTPLATE);
                            entries.add(ModItems.LUMINIS_LEGGINGS);
                            entries.add(ModItems.LUMINIS_BOOTS);
                        })
                        .build()
        );

        Registry.register(
                Registries.ITEM_GROUP,
                MUSIC_DISCS_TAB.getValue(),
                FabricItemGroup.builder()
                        .displayName(Text.translatable("itemGroup.arqueoutils.arqueo_discs"))
                        .icon(() -> new ItemStack(ModItems.PAINT_IT_BLACK_MUSIC_DISC))
                        .entries((context, entries) -> {
                            entries.add(ModItems.MEGALOVANIA_MUSIC_DISC);
                            entries.add(ModItems.DANCE_OF_THE_KNIGHTS_MUSIC_DISC);
                            entries.add(ModItems.PAINT_IT_BLACK_MUSIC_DISC);
                            entries.add(ModItems.PORTAL_RADIO_MUSIC_DISC);
                            entries.add(ModItems.STILL_ALIVE_MUSIC_DISC);
                            entries.add(ModItems.BALATRO_MUSIC_DISC);
                            entries.add(ModItems.MALENIA_MUSIC_DISC);
                            entries.add(ModItems.TETRIS_A_MUSIC_DISC);
                            entries.add(ModItems.COGWORK_DANCERS_MUSIC_DISC);
                            entries.add(ModItems.MANTIS_LORDS_MUSIC_DISC);
                            entries.add(ModItems.CRYPTEQUE_MUSIC_DISC);
                            entries.add(ModItems.DARK_NIGHT_MUSIC_DISC);
                            entries.add(ModItems.ROARING_KNIGHT_MUSIC_DISC);
                            entries.add(ModItems.GOURMET_RACE_MUSIC_DISC);
                            entries.add(ModItems.GLORY_ARSTOTSZKA_MUSIC_DISC);
                            entries.add(ModItems.GERUDO_VALLEY_MUSIC_DISC);
                        })
                        .build()
        );

        Registry.register(
                Registries.ITEM_GROUP,
                SCROLLS_TAB.getValue(),
                FabricItemGroup.builder()
                        .displayName(Text.translatable("itemGroup.arqueoutils.scrolls"))
                        .icon(() -> new ItemStack(ModItems.SCROLL))
                        .entries((context, entries) -> {
                            entries.add(ModItems.SCROLL);
                            for (String type : ScrollItem.getTypes()) {
                                for (int quality = 1; quality < 5; quality++) {
                                    ItemStack scroll = new ItemStack(ModItems.SCROLL);
                                    scroll.set(ModComponents.SCROLL_TYPE, type);
                                    scroll.set(ModComponents.SCROLL_QUALITY, quality);
                                    entries.add(scroll);
                                }
                            }
                        })
                        .build()
        );

        Registry.register(
                Registries.ITEM_GROUP,
                ORE_POWDER_TAB.getValue(),
                FabricItemGroup.builder()
                        .displayName(Text.translatable("itemGroup.ore_powder"))
                        .icon(() -> {
                            ItemStack stack = new ItemStack(ModItems.ORE_POWDER);
                            stack.set(ModComponents.ORE_POWDER_TYPE, "diamond");
                            return stack;
                        })
                        .entries((context, entries) -> {
                            entries.add(ModItems.ORE_POWDER_BAG);
                            for (String powderType : OrePowderItem.getTypes()) {
                                ItemStack powder = new ItemStack(ModItems.ORE_POWDER);
                                powder.set(ModComponents.ORE_POWDER_TYPE, powderType);
                                entries.add(powder);
                            }
                        })
                        .build()
        );
    }

    private ModItemGroups() {}
}
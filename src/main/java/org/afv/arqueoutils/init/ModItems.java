package org.afv.arqueoutils.init;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DeathProtectionComponent;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.afv.arqueoutils.ArqueoUtils;
import org.afv.arqueoutils.items.*;

import java.util.function.Function;

public final class ModItems {
    private ModItems() {}

    public static final Item MAGNET = register("magnet", MagnetItem::new, new Item.Settings());
    public static final Item VOID_TOTEM = register("void_idol", Item::new, new Item.Settings()
            .rarity(Rarity.RARE).maxCount(1).fireproof()
            .component(DataComponentTypes.DEATH_PROTECTION, DeathProtectionComponent.TOTEM_OF_UNDYING)
    );
    public static final Item BLOOD_SCYTHE = register("blood_scythe", BloodScythe::new, new Item.Settings()
            .sword(ToolMaterial.NETHERITE, 5, -2.4f)
            .component(ModComponents.SOULBOUND, true)
            .rarity(Rarity.EPIC)
            .component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, false));
    public static final Item DEATH_POUCH = register("death_pouch", DeathPouch::new, new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON));

    //<editor-fold desc="> AMULETS <">
    public static final Item EMPTY_AMULET = register(
            "empty_amulet",
            settings -> new AmuletItem("empty", settings), new Item.Settings()
    );
    public static final Item FORTUNE_AMULET = register(
            "fortune_amulet",
            settings -> new AmuletItem("fortune", settings), new Item.Settings()
    );
    public static final Item LOOTING_AMULET = register(
            "looting_amulet",
            settings -> new AmuletItem("looting", settings), new Item.Settings()
    );
    public static final Item SMELT_AMULET = register(
            "smelt_amulet",
            settings -> new AmuletItem("smelt", settings), new Item.Settings()
    );
    public static final Item POTION_AMULET = register(
            "potion_amulet",
            settings -> new AmuletItem("potion", settings), new Item.Settings()
    );
    public static final Item FORGE_AMULET = register(
            "forge_amulet",
            settings -> new AmuletItem("forge", settings), new Item.Settings()
    );
    //</editor-fold>

    public static final Item PERFECT_JADE = register("perfect_jade", Item::new, new Item.Settings()
            .rarity(Rarity.UNCOMMON)
            .component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
    );
    public static final Item BASTION_TREASURE = register("bastion_treasure", Item::new, new Item.Settings()
            .rarity(Rarity.UNCOMMON)
    );
    public static final Item BLAZING_HEART = register("blazing_heart", Item::new, new Item.Settings()
            .rarity(Rarity.UNCOMMON)
    );
    public static final Item ALCHEMIST_TOME = register("alchemist_tome", Item::new, new Item.Settings()
            .rarity(Rarity.UNCOMMON)
    );
    public static final Item WIND_RELIC = register("wind_relic", Item::new, new Item.Settings()
            .rarity(Rarity.UNCOMMON)
            .maxCount(1)
    );
    public static final Item WIND_TRIDENT = register("wind_trident", WindTridentItem::new, new Item.Settings());
    public static final Item ARCHAIC_DIAMOND = register("archaic_diamond", Item::new, new Item.Settings()
            .rarity(Rarity.UNCOMMON)
    );
    public static final Item SILVER_PLATES = register("silver_plates", Item::new, new Item.Settings()
            .rarity(Rarity.UNCOMMON)
    );
    public static final Item DEATH_BOOK = register("death_book", DeathBookItem::new, new Item.Settings());

    public static final Item EMPTY_TRIM = register("empty_trim_smithing_template", Item::new, new Item.Settings());
    public static final Item INVISIBLE_INK = register("invisible_ink", Item::new, new Item.Settings());
    public static final Item MODEL_CARD = register("model_card", ModelCard::new, new Item.Settings().maxCount(1));

    //<editor-fold desc="> LUMINIS <">
    public static final Item LUMINIS_CORE = register("luminis_core", Item::new, new Item.Settings());
    public static final Item LUMINIS_HELMET = register("luminis_helmet", Item::new, new Item.Settings()
            .armor(ModArmorMaterials.LUMINIS, EquipmentType.HELMET).fireproof());
    public static final Item LUMINIS_CHESTPLATE = register("luminis_chestplate", Item::new, new Item.Settings()
            .armor(ModArmorMaterials.LUMINIS, EquipmentType.CHESTPLATE).fireproof());
    public static final Item LUMINIS_LEGGINGS = register("luminis_leggings", Item::new, new Item.Settings()
            .armor(ModArmorMaterials.LUMINIS, EquipmentType.LEGGINGS).fireproof());
    public static final Item LUMINIS_BOOTS = register("luminis_boots", Item::new, new Item.Settings()
            .armor(ModArmorMaterials.LUMINIS, EquipmentType.BOOTS).fireproof());
    //</editor-fold>

    public static final Item SCROLL = register("upgrade_scroll", ScrollItem::new, new Item.Settings());

    public static final Item DARK_SPEAR = register("dark_spear", Item::new, new Item.Settings().spear(ToolMaterial.NETHERITE, 1.15F, 1.2F, 0.4F, 2.5F, 7.0F, 5.5F, 5.1F, 8.75F, 4.6F).fireproof());

    //<editor-fold desc="> DISCS <">
    public static final Item MEGALOVANIA_MUSIC_DISC = register("disc_megalovania",
            Item::new, new Item.Settings().jukeboxPlayable(ModSounds.MEGALOVANIA_KEY).maxCount(1).rarity(Rarity.RARE));
    public static final Item ROARING_KNIGHT_MUSIC_DISC = register("disc_roaring_knight",
            Item::new, new Item.Settings().jukeboxPlayable(ModSounds.ROARING_KNIGHT_KEY).maxCount(1).rarity(Rarity.RARE));
    public static final Item MALENIA_MUSIC_DISC = register("disc_malenia",
            Item::new, new Item.Settings().jukeboxPlayable(ModSounds.MALENIA_KEY).maxCount(1).rarity(Rarity.RARE));
    public static final Item DANCE_OF_THE_KNIGHTS_MUSIC_DISC = register("disc_dance_of_the_knights",
            Item::new, new Item.Settings().jukeboxPlayable(ModSounds.DANCE_OF_THE_KNIGHTS_KEY).maxCount(1).rarity(Rarity.RARE));
    public static final Item PAINT_IT_BLACK_MUSIC_DISC = register("disc_paint_it_black",
            Item::new, new Item.Settings().jukeboxPlayable(ModSounds.PAINT_IT_BLACK_KEY).maxCount(1).rarity(Rarity.RARE));
    public static final Item TETRIS_A_MUSIC_DISC = register("disc_tetris_a",
            Item::new, new Item.Settings().jukeboxPlayable(ModSounds.TETRIS_A_KEY).maxCount(1).rarity(Rarity.RARE));
    public static final Item COGWORK_DANCERS_MUSIC_DISC = register("disc_cogwork_dancers",
            Item::new, new Item.Settings().jukeboxPlayable(ModSounds.COGWORK_DANCERS_KEY).maxCount(1).rarity(Rarity.RARE));
    public static final Item MANTIS_LORDS_MUSIC_DISC = register("disc_mantis_lords",
            Item::new, new Item.Settings().jukeboxPlayable(ModSounds.MANTIS_LORDS_KEY).maxCount(1).rarity(Rarity.RARE));
    public static final Item CRYPTEQUE_MUSIC_DISC = register("disc_crypteque",
            Item::new, new Item.Settings().jukeboxPlayable(ModSounds.CRYPTEQUE_KEY).maxCount(1).rarity(Rarity.RARE));
    public static final Item DARK_NIGHT_MUSIC_DISC = register("disc_dark_night",
            Item::new, new Item.Settings().jukeboxPlayable(ModSounds.DARK_NIGHT_KEY).maxCount(1).rarity(Rarity.RARE));
    public static final Item STILL_ALIVE_MUSIC_DISC = register("disc_still_alive",
            Item::new, new Item.Settings().jukeboxPlayable(ModSounds.STILL_ALIVE_KEY).maxCount(1).rarity(Rarity.RARE));
    public static final Item PORTAL_RADIO_MUSIC_DISC = register("disc_portal_radio",
            Item::new, new Item.Settings().jukeboxPlayable(ModSounds.PORTAL_RADIO_KEY).maxCount(1).rarity(Rarity.RARE));
    public static final Item GERUDO_VALLEY_MUSIC_DISC = register("disc_gerudo_valley",
            Item::new, new Item.Settings().jukeboxPlayable(ModSounds.GERUDO_VALLEY_KEY).maxCount(1).rarity(Rarity.RARE));
    public static final Item GLORY_ARSTOTSZKA_MUSIC_DISC = register("disc_glory_arstotszka",
            Item::new, new Item.Settings().jukeboxPlayable(ModSounds.GLORY_ARSTOTSZKA_KEY).maxCount(1).rarity(Rarity.RARE));
    public static final Item GOURMET_RACE_MUSIC_DISC = register("disc_gourmet_race",
            Item::new, new Item.Settings().jukeboxPlayable(ModSounds.GOURMET_RACE_KEY).maxCount(1).rarity(Rarity.RARE));
    public static final Item BALATRO_MUSIC_DISC = register("disc_balatro",
            Item::new, new Item.Settings().jukeboxPlayable(ModSounds.BALATRO_KEY).maxCount(1).rarity(Rarity.RARE));
    //</editor-fold>

    public static final Item ORE_POWDER = register("ore_powder", OrePowderItem::new, new Item.Settings());
    public static final Item ORE_POWDER_BAG = register("ore_powder_bag", OrePowderBagItem::new, new Item.Settings().maxCount(1).rarity(Rarity.RARE).fireproof().component(ModComponents.SOULBOUND, true));

    public static Item register(String path, Function<Item.Settings, Item> factory, Item.Settings settings) {
        final RegistryKey<Item> registryKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ArqueoUtils.ModID, path));
        return Items.register(registryKey, factory, settings);
    }

    public static void initialize() {
    }
}

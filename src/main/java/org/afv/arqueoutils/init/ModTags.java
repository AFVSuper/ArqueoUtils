package org.afv.arqueoutils.init;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;

public class ModTags {
    public static class Blocks {

        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, Identifier.of(ArqueoUtils.ModID, name));
        }
    }
    public static class Items {
        public static final TagKey<Item> REPAIRS_LUMINIS_ARMOR = createTag("repairs_luminis_armor");
        public static final TagKey<Item> SCROLL_UPGRADE_ITEMS = createTag("scroll_upgradable_items");
        public static final TagKey<Item> SCROLL_DAMAGE_ITEMS = createTag("scroll_damage_items");
        public static final TagKey<Item> SCROLL_ARMOR_ITEMS = createTag("scroll_armor_items");
        public static final TagKey<Item> SCROLL_RANGE_ITEMS = createTag("scroll_range_items");
        public static final TagKey<Item> SCROLL_OXYGEN_ITEMS = createTag("scroll_oxygen_items");
        public static final TagKey<Item> SCROLL_SPEED_ITEMS = createTag("scroll_speed_items");

        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, Identifier.of(ArqueoUtils.ModID, name));
        }
    }
}

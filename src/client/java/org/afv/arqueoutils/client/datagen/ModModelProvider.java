package org.afv.arqueoutils.client.datagen;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.*;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.SelectItemModel;
import net.minecraft.client.render.item.property.select.TrimMaterialProperty;
import net.minecraft.client.render.item.tint.DyeTintSource;
import net.minecraft.client.render.item.tint.TintSource;
import net.minecraft.item.Item;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.equipment.trim.ArmorTrimMaterial;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.client.models.ModModels;
import org.afv.arqueoutils.init.ModBlocks;
import org.afv.arqueoutils.init.ModEquipmentAssetKeys;
import org.afv.arqueoutils.init.ModItems;

import java.util.ArrayList;
import java.util.List;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSingleton(ModBlocks.ELYTRA_DISPENSER, TexturedModel.CUBE_COLUMN);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        // TOOLS
        itemModelGenerator.register(ModItems.MAGNET, Models.GENERATED);
        itemModelGenerator.register(ModItems.DEATH_BOOK, Models.GENERATED);
        itemModelGenerator.register(ModItems.VOID_TOTEM, Models.GENERATED);
        itemModelGenerator.register(ModItems.DEATH_POUCH, Models.GENERATED);

        // DISCS
        itemModelGenerator.register(ModItems.MEGALOVANIA_MUSIC_DISC, Models.GENERATED);
        itemModelGenerator.register(ModItems.DANCE_OF_THE_KNIGHTS_MUSIC_DISC, Models.GENERATED);
        itemModelGenerator.register(ModItems.PAINT_IT_BLACK_MUSIC_DISC, Models.GENERATED);
        itemModelGenerator.register(ModItems.PORTAL_RADIO_MUSIC_DISC, Models.GENERATED);
        itemModelGenerator.register(ModItems.STILL_ALIVE_MUSIC_DISC, Models.GENERATED);
        itemModelGenerator.register(ModItems.BALATRO_MUSIC_DISC, Models.GENERATED);
        itemModelGenerator.register(ModItems.MALENIA_MUSIC_DISC, Models.GENERATED);
        itemModelGenerator.register(ModItems.TETRIS_A_MUSIC_DISC, Models.GENERATED);
        itemModelGenerator.register(ModItems.COGWORK_DANCERS_MUSIC_DISC, Models.GENERATED);
        itemModelGenerator.register(ModItems.MANTIS_LORDS_MUSIC_DISC, Models.GENERATED);
        itemModelGenerator.register(ModItems.CRYPTEQUE_MUSIC_DISC, Models.GENERATED);
        itemModelGenerator.register(ModItems.DARK_NIGHT_MUSIC_DISC, Models.GENERATED);
        itemModelGenerator.register(ModItems.ROARING_KNIGHT_MUSIC_DISC, Models.GENERATED);
        itemModelGenerator.register(ModItems.GOURMET_RACE_MUSIC_DISC, Models.GENERATED);
        itemModelGenerator.register(ModItems.GLORY_ARSTOTSZKA_MUSIC_DISC, Models.GENERATED);
        itemModelGenerator.register(ModItems.GERUDO_VALLEY_MUSIC_DISC, Models.GENERATED);

        // INGREDIENTS
        itemModelGenerator.register(ModItems.PERFECT_JADE, Models.GENERATED);
        itemModelGenerator.register(ModItems.BASTION_TREASURE, Models.GENERATED);
        itemModelGenerator.register(ModItems.ALCHEMIST_TOME, Models.GENERATED);
        itemModelGenerator.register(ModItems.WIND_RELIC, Models.GENERATED);
        itemModelGenerator.register(ModItems.ARCHAIC_DIAMOND, Models.GENERATED);
        itemModelGenerator.register(ModItems.BLAZING_HEART, Models.GENERATED);
        itemModelGenerator.register(ModItems.SILVER_PLATES, Models.GENERATED);
        itemModelGenerator.register(ModItems.LUMINIS_CORE, Models.GENERATED);
        // itemModelGenerator.register(ModItems.LUMINIS_UPGRADE_SMITHING_TEMPLATE, Models.GENERATED);

        // MISC
        itemModelGenerator.register(ModItems.EMPTY_TRIM, Models.GENERATED);
        itemModelGenerator.register(ModItems.INVISIBLE_INK, Models.GENERATED);
        itemModelGenerator.register(ModItems.MODEL_CARD, Models.GENERATED);

        // AMULETS
        itemModelGenerator.register(ModItems.EMPTY_AMULET, ModModels.AMULET_MODEL);
        itemModelGenerator.register(ModItems.FORTUNE_AMULET, ModModels.AMULET_MODEL);
        itemModelGenerator.register(ModItems.LOOTING_AMULET, ModModels.AMULET_MODEL);
        itemModelGenerator.register(ModItems.SMELT_AMULET, ModModels.AMULET_MODEL);
        itemModelGenerator.register(ModItems.POTION_AMULET, ModModels.AMULET_MODEL);
        itemModelGenerator.register(ModItems.FORGE_AMULET, ModModels.AMULET_MODEL);

        // ARMOR
        registerDyeArmor(itemModelGenerator, ModItems.LUMINIS_HELMET, ModEquipmentAssetKeys.LUMINIS, ItemModelGenerator.HELMET_TRIM_ID_PREFIX, -1);
        registerDyeArmor(itemModelGenerator, ModItems.LUMINIS_CHESTPLATE, ModEquipmentAssetKeys.LUMINIS, ItemModelGenerator.CHESTPLATE_TRIM_ID_PREFIX, -1);
        registerDyeArmor(itemModelGenerator, ModItems.LUMINIS_LEGGINGS, ModEquipmentAssetKeys.LUMINIS, ItemModelGenerator.LEGGINGS_TRIM_ID_PREFIX, -1);
        registerDyeArmor(itemModelGenerator, ModItems.LUMINIS_BOOTS, ModEquipmentAssetKeys.LUMINIS, ItemModelGenerator.BOOTS_TRIM_ID_PREFIX, -1);
    }

    public final void registerDyeArmor(ItemModelGenerator itemModelGenerator, Item item, RegistryKey<EquipmentAsset> equipmentKey, Identifier trimIdPrefix, int defaultColor) {
        Identifier identifier = ModelIds.getItemModelId(item);
        Identifier identifier2 = TextureMap.getId(item);
        Identifier identifier3 = TextureMap.getSubId(item, "_overlay");
        List<SelectItemModel.SwitchCase<RegistryKey<ArmorTrimMaterial>>> list = new ArrayList<>(ItemModelGenerator.TRIM_MATERIALS.size());

        for(ItemModelGenerator.TrimMaterial trimMaterial : ItemModelGenerator.TRIM_MATERIALS) {
            Identifier identifier4 = identifier.withSuffixedPath("_" + trimMaterial.assets().base().suffix() + "_trim");
            String var10001 = trimMaterial.assets().getAssetId(equipmentKey).suffix();
            Identifier identifier5 = trimIdPrefix.withSuffixedPath("_" + var10001);
            ItemModel.Unbaked unbaked;
            itemModelGenerator.uploadArmor(identifier4, identifier2, identifier3, identifier5);
            unbaked = ItemModels.tinted(identifier4, new TintSource[]{new DyeTintSource(defaultColor)});

            list.add(ItemModels.switchCase(trimMaterial.materialKey(), unbaked));
        }

        ItemModel.Unbaked unbaked2;
        Models.GENERATED_TWO_LAYERS.upload(identifier, TextureMap.layered(identifier2, identifier3), itemModelGenerator.modelCollector);
        unbaked2 = ItemModels.tinted(identifier, new TintSource[]{new DyeTintSource(defaultColor)});

        itemModelGenerator.output.accept(item, ItemModels.select(new TrimMaterialProperty(), unbaked2, list));
    }
}

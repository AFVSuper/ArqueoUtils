package org.afv.arqueoutils.init;

import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;

public interface ModEquipmentAssetKeys {
    RegistryKey<EquipmentAsset> LUMINIS = register("luminis");

    static RegistryKey<EquipmentAsset> register(String name) {
        return RegistryKey.of(EquipmentAssetKeys.REGISTRY_KEY, Identifier.of(ArqueoUtils.ModID, name));
    }
}

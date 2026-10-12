package org.afv.arqueoutils.init;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;
import org.afv.arqueoutils.entity.TombstoneEntity;

public class ModEntities {
    public static final EntityType<TombstoneEntity> TOMBSTONE =
            Registry.register(
                    Registries.ENTITY_TYPE,
                    Identifier.of(ArqueoUtils.ModID, "tombstone"),
                    EntityType.Builder
                            .create(TombstoneEntity::new, SpawnGroup.MISC)
                            .dimensions(0.75f, 0.75f)
                            .maxTrackingRange(10)
                            .trackingTickInterval(1)
                            .build(RegistryKey.of(Registries.ENTITY_TYPE.getKey(), Identifier.of(ArqueoUtils.ModID, "tombstone")))
            );

    public static void init() {}
}

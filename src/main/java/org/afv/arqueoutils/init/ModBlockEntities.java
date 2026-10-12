package org.afv.arqueoutils.init;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;
import org.afv.arqueoutils.blocks.CoreBlockEntity;
import org.afv.arqueoutils.blocks.ElytraDispenserBlockEntity;

public class ModBlockEntities {

    public static final BlockEntityType<ElytraDispenserBlockEntity> ELYTRA_DISPENSER =
        Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(ArqueoUtils.ModID, "elytra_dispenser"),
            FabricBlockEntityTypeBuilder
                .create(ElytraDispenserBlockEntity::new, ModBlocks.ELYTRA_DISPENSER)
                .build()
        );

    public static final BlockEntityType<CoreBlockEntity> CTM_CORE =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(ArqueoUtils.ModID, "ctm_core"),
                    FabricBlockEntityTypeBuilder
                            .create(CoreBlockEntity::new, ModBlocks.CTM_CORE)
                            .build()
            );

    public static void init() {}
}
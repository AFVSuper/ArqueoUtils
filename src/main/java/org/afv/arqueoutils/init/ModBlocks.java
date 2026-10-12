package org.afv.arqueoutils.init;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;
import org.afv.arqueoutils.blocks.CoreBlock;
import org.afv.arqueoutils.blocks.ElytraDispenserBlock;

import java.util.function.Function;

public class ModBlocks {
//    public static final Block ELYTRA_DISPENSER = registerBlock("elytra_dispenser",
//            new ElytraDispenserBlock(AbstractBlock.Settings.create()));
    public static final Identifier ELYTRA_DISPENSER_ID =
        Identifier.of(ArqueoUtils.ModID, "elytra_dispenser");
    public static final Identifier CORE_ID =
            Identifier.of(ArqueoUtils.ModID, "ctm_core");

    public static final ElytraDispenserBlock ELYTRA_DISPENSER =
            Registry.register(
                    Registries.BLOCK,
                    ELYTRA_DISPENSER_ID,
                    new ElytraDispenserBlock(
                            AbstractBlock.Settings
                                    .create()
                                    .registryKey(RegistryKey.of(Registries.BLOCK.getKey(), ELYTRA_DISPENSER_ID))
                                    .strength(-1f)
                    )
            );

    public static final CoreBlock CTM_CORE =
            Registry.register(
                    Registries.BLOCK,
                    CORE_ID,
                    new CoreBlock(
                            AbstractBlock.Settings
                                    .create()
                                    .registryKey(RegistryKey.of(Registries.BLOCK.getKey(), CORE_ID))
                                    .strength(-1f)
                    )
            );

    static {
        Items.register(ELYTRA_DISPENSER);
        Items.register(CTM_CORE);
    }

    private static Block register(String path, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        final Identifier identifier = Identifier.of(ArqueoUtils.ModID, path);
        final RegistryKey<Block> registryKey = RegistryKey.of(RegistryKeys.BLOCK, identifier);

        final Block block = Blocks.register(registryKey, factory, settings);
        Items.register(block);
        return block;
    }

    public static void initialize() {}
}

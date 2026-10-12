package org.afv.arqueoutils.init;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;
import org.afv.arqueoutils.screen.OrePowderBagScreenHandler;

public final class ModScreenHandlers {
    public static final ScreenHandlerType<OrePowderBagScreenHandler> ORE_POWDER_BAG = Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of(ArqueoUtils.ModID, "ore_powder_bag"),
            new ScreenHandlerType<>(OrePowderBagScreenHandler::new, FeatureSet.of(FeatureFlags.VANILLA))
    );

    private ModScreenHandlers() {}

    public static void init() {}
}

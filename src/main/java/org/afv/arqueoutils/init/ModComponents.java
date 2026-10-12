package org.afv.arqueoutils.init;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;
import org.afv.arqueoutils.items.OrePowderBagData;

public class ModComponents {

    public static final ComponentType<Boolean> MAGNET_ENABLED =
            Registry.register(
                    Registries.DATA_COMPONENT_TYPE,
                    Identifier.of(ArqueoUtils.ModID, "magnet_enabled"),
                    ComponentType.<Boolean>builder().codec(Codec.BOOL).build()
            );

    public static final ComponentType<Boolean> SOULBOUND =
            Registry.register(
                    Registries.DATA_COMPONENT_TYPE,
                    Identifier.of(ArqueoUtils.ModID, "soulbound"),
                    ComponentType.<Boolean>builder().codec(Codec.BOOL).build()
            );

    public static final ComponentType<Boolean> INVISIBLE_DYED =
            Registry.register(
                    Registries.DATA_COMPONENT_TYPE,
                    Identifier.of(ArqueoUtils.ModID, "invisible_dyed"),
                    ComponentType.<Boolean>builder().codec(Codec.BOOL).build()
            );

    public static final ComponentType<String> SCROLL_TYPE =
            Registry.register(
                    Registries.DATA_COMPONENT_TYPE,
                    Identifier.of(ArqueoUtils.ModID, "scroll_type"),
                    ComponentType.<String>builder().codec(Codec.STRING).build()
            );

    public static final ComponentType<Integer> SCROLL_QUALITY =
            Registry.register(
                    Registries.DATA_COMPONENT_TYPE,
                    Identifier.of(ArqueoUtils.ModID, "scroll_quality"),
                    ComponentType.<Integer>builder().codec(Codec.INT).build()
            );

    public record ScrollUpgrade(String type, int quality) {}

    public static final ComponentType<ScrollUpgrade> SCROLL_UPGRADE =
            Registry.register(
                    Registries.DATA_COMPONENT_TYPE,
                    Identifier.of(ArqueoUtils.ModID, "scroll_upgrade"),
                    ComponentType.<ScrollUpgrade>builder()
                            .codec(Codec.STRING.xmap(
                                    s -> {
                                        var parts = s.split(":");
                                        if (parts.length != 2) return new ScrollUpgrade(null, 0);
                                        return new ScrollUpgrade(parts[0], Integer.parseInt(parts[1]));
                                    },
                                    upgrade -> upgrade.type + ":" + upgrade.quality
                            ))
                            .build()
            );

    public static final ComponentType<String> ORE_POWDER_TYPE =
            Registry.register(
                    Registries.DATA_COMPONENT_TYPE,
                    Identifier.of(ArqueoUtils.ModID, "ore_powder_type"),
                    ComponentType.<String>builder().codec(Codec.STRING).build()
            );

    public static final ComponentType<OrePowderBagData> ORE_POWDER_BAG =
            Registry.register(
                    Registries.DATA_COMPONENT_TYPE,
                    Identifier.of(ArqueoUtils.ModID, "ore_powder_bag"),
                    ComponentType.<OrePowderBagData>builder().codec(OrePowderBagData.CODEC).build()
            );

    public static final ComponentType<Integer> RIPTIDE_TRAIL_TICKS =
            Registry.register(
                    Registries.DATA_COMPONENT_TYPE,
                    Identifier.of(ArqueoUtils.ModID, "riptide_trail_ticks"),
                    ComponentType.<Integer>builder().codec(Codec.INT).build()
            );

    public static void init() {}
}
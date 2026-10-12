package org.afv.arqueoutils.potion;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;

public class ModPotions {

    public static final RegistryEntry<Potion> HASTE_POTION = registerPotion("haste_potion",
            new Potion("haste_potion", new StatusEffectInstance(StatusEffects.HASTE, 3600, 0)));
    public static final RegistryEntry<Potion> LONG_HASTE_POTION;
    public static final RegistryEntry<Potion> STRONG_HASTE_POTION;
    public static final RegistryEntry<Potion> NAUSEA_POTION;
    public static final RegistryEntry<Potion> BLINDNESS_POTION;
    public static final RegistryEntry<Potion> WITHER_POTION;
    public static final RegistryEntry<Potion> PANIC_POTION;
    public static final RegistryEntry<Potion> HEALTH_DISRUPTION_POTION;
    public static final RegistryEntry<Potion> LONG_HEALTH_DISRUPTION_POTION;
    public static final RegistryEntry<Potion> OXIDATION_POTION;
    public static final RegistryEntry<Potion> LONG_OXIDATION_POTION;

    private static RegistryEntry<Potion> registerPotion(String name, Potion potion) {
        return Registry.registerReference(Registries.POTION, Identifier.of(ArqueoUtils.ModID, name), potion);
    }

    public static void register() {}

    static {
        LONG_HASTE_POTION = registerPotion("long_haste_potion", new Potion("haste_potion", new StatusEffectInstance(StatusEffects.HASTE, 9600, 0)));
        STRONG_HASTE_POTION = registerPotion("strong_haste_potion", new Potion("haste_potion", new StatusEffectInstance(StatusEffects.HASTE, 1800, 1)));
        NAUSEA_POTION = registerPotion(
                "nausea_potion", new Potion(
                        "nausea_potion",
                        new StatusEffectInstance(StatusEffects.NAUSEA, 600, 0)
                )
        );
        BLINDNESS_POTION = registerPotion(
                "blindness_potion", new Potion(
                        "blindness_potion",
                        new StatusEffectInstance(StatusEffects.BLINDNESS, 600, 0)
                )
        );
        WITHER_POTION = registerPotion(
                "wither_potion", new Potion(
                        "wither_potion",
                        new StatusEffectInstance(StatusEffects.WITHER, 900, 0)
                )
        );
        PANIC_POTION = registerPotion(
                "panic_potion", new Potion(
                        "panic_potion",
                        new StatusEffectInstance(StatusEffects.INSTANT_HEALTH, 5, 3),
                        new StatusEffectInstance(StatusEffects.RESISTANCE, 1800, 0)
                )
        );
        HEALTH_DISRUPTION_POTION = registerPotion(
                "health_disruption_potion", new Potion(
                        "health_disruption_potion",
                        new StatusEffectInstance(ModEffects.HEALTH_DISRUPTION, 1200, 0)
                )
        );
        LONG_HEALTH_DISRUPTION_POTION = registerPotion(
                "long_health_disruption_potion", new Potion(
                        "health_disruption_potion",
                        new StatusEffectInstance(ModEffects.HEALTH_DISRUPTION, 3600, 0)
                )
        );
        OXIDATION_POTION = registerPotion(
                "oxidation_potion", new Potion(
                        "oxidation_potion",
                        new StatusEffectInstance(ModEffects.OXIDATION, 1200, 0)
                )
        );
        LONG_OXIDATION_POTION = registerPotion(
                "long_oxidation_potion", new Potion(
                        "oxidation_potion",
                        new StatusEffectInstance(ModEffects.OXIDATION, 3600, 0)
                )
        );
    }
}

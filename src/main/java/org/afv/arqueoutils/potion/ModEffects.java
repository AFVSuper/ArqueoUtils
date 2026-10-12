package org.afv.arqueoutils.potion;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;
import org.afv.arqueoutils.potion.effect.HealthDisruptionEffect;
import org.afv.arqueoutils.potion.effect.OxidationEffect;

public class ModEffects {
    public static RegistryEntry<StatusEffect> HEALTH_DISRUPTION = registerStatusEffect(
            "health_disruption",
            new HealthDisruptionEffect(StatusEffectCategory.HARMFUL, 0xa30000)
    );
    public static RegistryEntry<StatusEffect> OXIDATION = registerStatusEffect(
            "oxidation",
            new OxidationEffect(StatusEffectCategory.HARMFUL, 0x00b57c)
    );

    private static RegistryEntry<StatusEffect> registerStatusEffect(String name, StatusEffect statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(ArqueoUtils.ModID, name), statusEffect);
    }

    public static void init() {}
}

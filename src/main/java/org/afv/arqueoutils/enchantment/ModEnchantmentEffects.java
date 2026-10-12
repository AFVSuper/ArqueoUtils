package org.afv.arqueoutils.enchantment;

import com.mojang.serialization.MapCodec;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;
import org.afv.arqueoutils.enchantment.custom.MagnetPowerEnchantmentEffect;
import org.afv.arqueoutils.enchantment.custom.MagnetRadiusEnchantmentEffect;
import org.afv.arqueoutils.enchantment.custom.PulverizingEnchantmentEffect;

public class ModEnchantmentEffects {
    public static final MapCodec<? extends EnchantmentEntityEffect> MAGNET_POWER =
            registerEntityEffect("magnet_power", MagnetPowerEnchantmentEffect.CODEC);

    public static final MapCodec<? extends EnchantmentEntityEffect> MAGNET_RADIUS =
            registerEntityEffect("magnet_radius", MagnetRadiusEnchantmentEffect.CODEC);

    public static final MapCodec<? extends EnchantmentEntityEffect> PULVERIZING =
            registerEntityEffect("pulverizing", PulverizingEnchantmentEffect.CODEC);

    private static MapCodec<? extends EnchantmentEntityEffect> registerEntityEffect(String name,
                                                                                    MapCodec<? extends EnchantmentEntityEffect> codec) {
        return Registry.register(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, Identifier.of(ArqueoUtils.ModID, name), codec);
    }

    public static void register() {

    }
}

package org.afv.arqueoutils.enchantment.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.afv.arqueoutils.ArqueoUtils;
import org.afv.arqueoutils.init.ModAttributes;

public class MagnetPowerEnchantmentEffect implements EnchantmentEntityEffect {
    public static final MapCodec<MagnetPowerEnchantmentEffect> CODEC = MapCodec.unit(MagnetPowerEnchantmentEffect::new);
    private static final Identifier ENCH_MODIF_ID = Identifier.of(ArqueoUtils.ModID, "magnet_power_ench_mod");

    @Override
    public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos) {
        if (user instanceof PlayerEntity player) {
            EntityAttributeInstance powerAttr = player.getAttributeInstance(ModAttributes.MAGNETIC_POWER);

            if (powerAttr != null) {
                powerAttr.addPersistentModifier(
                        new EntityAttributeModifier(
                                ENCH_MODIF_ID,
                                0.25 * level,
                                EntityAttributeModifier.Operation.ADD_VALUE
                        )
                );
            }
        }
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> getCodec() {
        return CODEC;
    }
}

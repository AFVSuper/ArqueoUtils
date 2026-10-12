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

public class MagnetRadiusEnchantmentEffect implements EnchantmentEntityEffect {
    public static final MapCodec<MagnetRadiusEnchantmentEffect> CODEC = MapCodec.unit(MagnetRadiusEnchantmentEffect::new);
    private static final Identifier ENCH_MODIF_ID = Identifier.of(ArqueoUtils.ModID, "magnet_radius_ench_mod");

    @Override
    public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos) {
        if (user instanceof PlayerEntity player) {
            EntityAttributeInstance radiusAttr = player.getAttributeInstance(ModAttributes.MAGNETIC_RADIUS);

            if (radiusAttr != null) {
                radiusAttr.addPersistentModifier(
                        new EntityAttributeModifier(
                                ENCH_MODIF_ID,
                                2 * level,
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

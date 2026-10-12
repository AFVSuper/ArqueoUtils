package org.afv.arqueoutils.enchantment.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

/**
 * Pulverizing replaces block drops for supported ores (see {@link org.afv.arqueoutils.mixin.BlockMixin}).
 * This effect is registered on {@link net.minecraft.component.EnchantmentEffectComponentTypes#HIT_BLOCK}
 * for codec/datapack parity; the apply hook is intentionally empty because drop replacement runs in block loot resolution.
 */
public class PulverizingEnchantmentEffect implements EnchantmentEntityEffect {
    public static final MapCodec<PulverizingEnchantmentEffect> CODEC = MapCodec.unit(PulverizingEnchantmentEffect::new);

    @Override
    public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos) {
        // Drop replacement is handled in Block.getDroppedStacks mixin (needs full loot result for Fortune).
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> getCodec() {
        return CODEC;
    }
}

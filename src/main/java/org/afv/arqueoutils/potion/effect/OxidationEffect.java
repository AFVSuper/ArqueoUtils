package org.afv.arqueoutils.potion.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;

public class OxidationEffect extends StatusEffect {
    public OxidationEffect(StatusEffectCategory category, int color) {
        super(category, color);

        this.addAttributeModifier(
                EntityAttributes.ARMOR,
                Identifier.of(ArqueoUtils.ModID, "oxidation_armor"),
                -0.2,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );

        this.addAttributeModifier(
                EntityAttributes.ARMOR_TOUGHNESS,
                Identifier.of(ArqueoUtils.ModID, "oxidation_toughness"),
                -0.2,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
    }

    @Override
    public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
        return super.applyUpdateEffect(world, entity, amplifier);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}

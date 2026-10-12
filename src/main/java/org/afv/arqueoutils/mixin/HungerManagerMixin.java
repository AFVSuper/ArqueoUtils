package org.afv.arqueoutils.mixin;

import net.minecraft.entity.player.HungerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.rule.GameRule;
import net.minecraft.world.rule.GameRules;
import org.afv.arqueoutils.potion.ModEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HungerManager.class)
public class HungerManagerMixin {
    @Redirect(
            method = "update",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/rule/GameRules;getValue(Lnet/minecraft/world/rule/GameRule;)Ljava/lang/Object;"
            )
    )
    private Object arqueo$disableNaturalRegen(
            GameRules instance, GameRule<Boolean> rule, ServerPlayerEntity player
    ) {
        // Only intercept the natural regen gamerule
        if (rule == GameRules.NATURAL_HEALTH_REGENERATION &&
                player.hasStatusEffect(ModEffects.HEALTH_DISRUPTION)) {
            return false; // force disable
        }

        return instance.getValue(rule);
    }
}

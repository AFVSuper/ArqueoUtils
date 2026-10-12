package org.afv.arqueoutils.client.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.StatusEffectsDisplay;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.text.Text;
import org.afv.arqueoutils.init.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(StatusEffectsDisplay.class)
public class StatusEffectsDisplayMixin {
    @Redirect(
            method = "drawStatusEffects",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/effect/StatusEffectUtil;getDurationText(Lnet/minecraft/entity/effect/StatusEffectInstance;FF)Lnet/minecraft/text/Text;"
            )
    )
    private Text redirectGetDurationText(StatusEffectInstance effectInstance, float scale, float tickRate) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player != null) {
            boolean hasSpecialItem = client.player.getOffHandStack().isOf(ModItems.POTION_AMULET);

            if (hasSpecialItem) {
                StatusEffect type = effectInstance.getEffectType().value();
                if (type.isBeneficial()) return StatusEffectUtil.getDurationText(effectInstance, scale, tickRate / 2);
                else return StatusEffectUtil.getDurationText(effectInstance, scale, tickRate * 2);
            }
        }

        // fallback to vanilla
        return StatusEffectUtil.getDurationText(effectInstance, scale, tickRate);
    }
}

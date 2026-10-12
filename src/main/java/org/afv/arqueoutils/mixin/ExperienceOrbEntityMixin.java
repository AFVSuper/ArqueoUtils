package org.afv.arqueoutils.mixin;

import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.server.MinecraftServer;
import org.afv.arqueoutils.world.ArqueoWorldState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ExperienceOrbEntity.class)
public abstract class ExperienceOrbEntityMixin {

    @ModifyConstant(method = "onPlayerCollision", constant = @Constant(intValue = 2))
    private int fasterXpPickup(int originalDelay) {
        MinecraftServer server = ((ExperienceOrbEntity) (Object) this).getEntityWorld().getServer();
        if (server == null) return originalDelay;
        return ArqueoWorldState.get(server).isFastXpPickup() ? 0 : originalDelay;
    }
}
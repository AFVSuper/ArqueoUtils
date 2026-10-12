package org.afv.arqueoutils.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.server.world.ServerWorld;
import org.afv.arqueoutils.util.SoulboundUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {

    @Inject(method = "spawnEntity", at = @At("HEAD"), cancellable = true)
    private void spawnEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof ItemEntity item && SoulboundUtil.isStackSoulbound(item.getStack()) && item.getOwner() != null && !item.getOwner().isAlive()) {
            cir.setReturnValue(false);
        }
    }

}
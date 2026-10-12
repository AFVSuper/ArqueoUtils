package org.afv.arqueoutils.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import org.afv.arqueoutils.holder.WindTridentHolder;
import org.afv.arqueoutils.items.WindTridentItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TridentEntity.class)
public class TridentEntityMixin implements WindTridentHolder {
    @Unique
    private boolean windTrident = false;

    @Inject(method = "tick", at = @At("HEAD"))
    public void flyParticles(CallbackInfo ci) {
        if (!windTrident) return;
        TridentEntity trident = (TridentEntity) (Object) this;
        if (trident.getVelocity().length() == 0) return;
        ServerWorld world = (ServerWorld) trident.getEntityWorld();
        world.spawnParticles(
                ParticleTypes.END_ROD,
                true,
                false,
                trident.getX(),
                trident.getBodyY(0.5),
                trident.getZ(),
                2,
                0, 0, 0,
                0.02
        );

        world.spawnParticles(
                ParticleTypes.CLOUD,
                true,
                false,
                trident.getX(),
                trident.getBodyY(0.5),
                trident.getZ(),
                1,
                0, 0, 0,
                0.0
        );
    }

    @Inject(method = "onEntityHit", at = @At("TAIL"))
    public void onHit(EntityHitResult entityHitResult, CallbackInfo ci) {
        TridentEntity trident = (TridentEntity) (Object) this;
        if (!windTrident || !(trident.getEntityWorld() instanceof ServerWorld world)) return;

        explode(world, trident);
    }

    @Inject(method = "onBlockHitEnchantmentEffects", at = @At("TAIL"))
    public void onBlockHit(ServerWorld world, BlockHitResult blockHitResult, ItemStack weaponStack, CallbackInfo ci) {
        TridentEntity trident = (TridentEntity) (Object) this;
        if (!windTrident) return;

        explode(world, trident);
    }

    @Unique
    private void explode(ServerWorld world, TridentEntity trident) {
        double radius = WindTridentItem.getKnockbackRadius();
        Entity owner = trident.getOwner();

        // ArqueoUtils.LOGGER.info("{}", world.getOtherEntities(trident, trident.getBoundingBox().expand(radius)));
        world.getOtherEntities(trident, trident.getBoundingBox().expand(radius)).forEach(e -> {
            Vec3d dir = e.getEntityPos().subtract(trident.getEntityPos()).normalize();
            if (e instanceof ServerPlayerEntity serverPlayer) {
                if (owner == null || !owner.getUuid().equals(serverPlayer.getUuid())) {
                    serverPlayer.addVelocity(new Vec3d(dir.x * 1.5, 0.5, dir.z * 1.5));
                    serverPlayer.networkHandler.sendPacket(
                            new EntityVelocityUpdateS2CPacket(serverPlayer)
                    );
                }
            } else {
                e.addVelocity(dir.x * 1.5, 0.5, dir.z * 1.5);
            }
        });

        world.spawnParticles(
                ParticleTypes.GUST_EMITTER_SMALL,
                true,
                false,
                trident.getX(),
                trident.getY(),
                trident.getZ(),
                1,
                0, 0, 0,
                0
        );

        world.playSoundFromEntity(null, trident, SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST.value(), SoundCategory.PLAYERS, 1, 1);
        world.playSoundFromEntity(null, trident, SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.PLAYERS, 1, 1.5f);
    }

    @Override
    public boolean isWindTrident() {
        return windTrident;
    }

    @Override
    public void setWindTrident(boolean windTrident) {
        this.windTrident = windTrident;
    }
}

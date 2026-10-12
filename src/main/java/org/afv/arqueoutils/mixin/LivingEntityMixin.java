package org.afv.arqueoutils.mixin;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;
import org.afv.arqueoutils.ArqueoUtils;
import org.afv.arqueoutils.util.EquipmentHelper;
import org.afv.arqueoutils.world.CTMState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;

import static org.afv.arqueoutils.util.SoulboundUtil.isStackSoulbound;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "canGlide", at = @At("RETURN"), cancellable = true)
    private void onCanGlide(CallbackInfoReturnable<Boolean> cir) {
        if (!ArqueoUtils.ENABLED_COLYTRA) return;
        LivingEntity e = (LivingEntity) (Object) this;
        if (!e.isOnGround() && !e.hasVehicle() && !e.hasStatusEffect(StatusEffects.LEVITATION)) {
            cir.setReturnValue(getItemInElytraSlot().contains(DataComponentTypes.GLIDER));
        } else {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "dropLoot*", at = @At("TAIL"))
    private void arqueoutils$dropLoot(ServerWorld world,
                                      DamageSource damageSource,
                                      boolean causedByPlayer,
                                      CallbackInfo ci) {

        LivingEntity entity = (LivingEntity) (Object) this;

        if (!(entity instanceof WitherSkeletonEntity)) {
            return;
        }

        if (!(damageSource.getAttacker() instanceof ServerPlayerEntity player)) {
            return;
        }

        int kills = player.getStatHandler().getStat(Stats.KILLED.getOrCreateStat(EntityType.WITHER_SKELETON));

        MinecraftServer server = player.getEntityWorld().getServer();
        if (server == null) return;
        CTMState state = CTMState.get(server);

        if (kills != 40 || state.hasWitherSkeletonObtained()) {
            return;
        }

        entity.dropStack(world, new ItemStack(Items.WITHER_SKELETON_SKULL));
    }

    @Unique
    public ItemStack getItemInElytraSlot() {
        LivingEntity e = (LivingEntity) (Object) this;
        if (ArqueoUtils.ENABLED_COLYTRA) {
            if (!(e instanceof PlayerEntity player)) return null;
            return player.getInventory().getStack(42);
        } else {
            return e.getEquippedStack(EquipmentSlot.CHEST);
        }
    }

    @Inject(
            method = "tickGliding",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/Util;getRandom(Ljava/util/List;Lnet/minecraft/util/math/random/Random;)Ljava/lang/Object;"
            ),
            cancellable = true
    )
    private void handleCustomElytra(CallbackInfo ci) {
        if (!ArqueoUtils.ENABLED_COLYTRA) return;
        // ArqueoUtils.LOGGER.info("CHECK CUSTOM 0");

        LivingEntity self = (LivingEntity)(Object)this;

        if (!(self instanceof PlayerEntity player)) {
            return;
        }

        ItemStack customElytra = getItemInElytraSlot();

        if (!customElytra.contains(DataComponentTypes.GLIDER)) {
            return;
        }

        // ArqueoUtils.LOGGER.info("CHECK CUSTOM");

        // --- Mirror vanilla timing ---
        int i = player.getGlidingTicks() + 1;
        if (i % 10 == 0) {
            int j = i / 10;
            if (j % 2 == 0) {
                if (customElytra.contains(DataComponentTypes.MAX_DAMAGE)) {
                    customElytra.damage(
                            1,
                            player
                    );
                }
            }
        }

        // Vanilla already emitted the event at this point
        ci.cancel(); // CRITICAL: stops empty-list crash
    }

    @Inject(method = "canEquip", at = @At("HEAD"), cancellable = true)
    private void blockElytraInChest(ItemStack stack, EquipmentSlot slot, CallbackInfoReturnable<Boolean> cir) {
        // If chest slot AND item is Elytra, deny it
        if (!ArqueoUtils.ENABLED_COLYTRA) return;
        if (slot == EquipmentSlot.CHEST && stack.isOf(Items.ELYTRA)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "tickStatusEffects",
            at = @At("HEAD"),
            cancellable = true
    )
    private void modifyPotionTicking(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (!EquipmentHelper.hasPotionAmulet(entity)) {
            return; // let vanilla logic run
        }

        ci.cancel(); // fully replace logic

        Iterator<StatusEffectInstance> iterator =
                entity.getStatusEffects().iterator();

        while (iterator.hasNext()) {
            StatusEffectInstance effect = iterator.next();
            StatusEffect type = effect.getEffectType().value();

            boolean beneficial = type.isBeneficial();
            int age = this.age;

            int decrement = 1;

            if (beneficial) {
                // decrease 1s every 2s → 1 tick every 40 ticks
                if (age % 2 != 0) continue;
            } else {
                // decrease 2s every 1s → 2 ticks every tick
                decrement = 2;
            }

            StatusEffectInstanceAccessor accessor =
                    (StatusEffectInstanceAccessor) effect;

            int duration = effect.getDuration();

            duration -= decrement;

            accessor.setDuration(duration);

            if (duration <= 0) {
                iterator.remove();
                type.onRemoved(entity.getAttributes());
            }
            if (type.canApplyUpdateEffect(duration, effect.getAmplifier())) {
                if (!entity.getEntityWorld().isClient()) {
                    ServerWorld world = (ServerWorld) entity.getEntityWorld();
                    type.applyUpdateEffect(world, entity, effect.getAmplifier());
                }
            }
        }
    }

    @Shadow
    @Nullable
    protected abstract ItemEntity createItemEntity(ItemStack stack, boolean atSelf, boolean retainOwnership);

    @Inject(method = "dropItem", at = @At("HEAD"), cancellable = true)
    private void dropItem(ItemStack stack, boolean dropAtSelf, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> cir) {
        if (isStackSoulbound(stack)) {
            ItemEntity itemEntity = createItemEntity(stack, false, true);
            if (itemEntity != null) {
                itemEntity.setOwner(getUuid());
                getEntityWorld().spawnEntity(itemEntity);
            }

            cir.setReturnValue(itemEntity);
        }
    }
}
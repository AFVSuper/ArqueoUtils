package org.afv.arqueoutils.items;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.WeaponComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.TridentItem;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.afv.arqueoutils.holder.AirRiptideHolder;
import org.afv.arqueoutils.holder.WindTridentHolder;

public class WindTridentItem extends TridentItem {
    private static final double KNOCKBACK_RADIUS = 4.0;
    private static final int WIND_COST = 2;
    public static double getKnockbackRadius() { return KNOCKBACK_RADIUS; }

    public WindTridentItem(Item.Settings settings) {
        super(settings.maxCount(1)
                .rarity(Rarity.RARE)
                .maxDamage(300)
                .attributeModifiers(TridentItem.createAttributeModifiers())
                .component(DataComponentTypes.TOOL, TridentItem.createToolComponent())
                .enchantable(1)
                .component(DataComponentTypes.WEAPON, new WeaponComponent(1))
        );
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (stack.willBreakNextUse()) {
            return ActionResult.FAIL;
        } else if (EnchantmentHelper.getTridentSpinAttackStrength(stack, user) > 0.0F && !user.isTouchingWaterOrRain()) {
            if (!hasWindCharges(user) || user.getGameMode() == GameMode.CREATIVE) {
                return ActionResult.FAIL;
            }
            user.setCurrentHand(hand);
            return ActionResult.CONSUME;
        } else {
            user.setCurrentHand(hand);
            return ActionResult.CONSUME;
        }
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity playerEntity) {
            int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
            if (i < 10) {
                return false;
            } else {
                float f = EnchantmentHelper.getTridentSpinAttackStrength(stack, playerEntity);
                if (f > 0.0F && !playerEntity.isTouchingWaterOrRain()) {
                    boolean bl = playerEntity.getGameMode() == GameMode.CREATIVE;

                    if (!hasWindCharges(playerEntity) && !bl) {
                        return false;
                    }

                    if (world instanceof ServerWorld serverWorld) {
                        if (!bl) consumeWindCharges(playerEntity);
                        serverWorld.spawnParticles(
                                ParticleTypes.GUST_EMITTER_SMALL,
                                true,
                                false,
                                playerEntity.getX(),
                                playerEntity.getY(),
                                playerEntity.getZ(),
                                1,
                                0,
                                0,
                                0,
                                0
                        );
                        serverWorld.playSound(null, playerEntity.getBlockPos(), SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST.value(), SoundCategory.PLAYERS, 1f, 1f);
                        if (!bl) playerEntity.getItemCooldownManager().set(stack, 120);
                        if (playerEntity instanceof AirRiptideHolder holder) holder.setAirRiptideTicks(20);
                    }
                    return riptide(playerEntity, f, stack, world, true);
                } else if (stack.willBreakNextUse()) {
                    return false;
                } else {
                    RegistryEntry<SoundEvent> registryEntry = EnchantmentHelper.getEffect(stack, EnchantmentEffectComponentTypes.TRIDENT_SOUND).orElse(SoundEvents.ITEM_TRIDENT_THROW);
                    playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
                    if (world instanceof ServerWorld serverWorld) {
                        stack.damage(1, playerEntity);
                        if (f == 0.0F) {
                            ItemStack itemStack = stack.splitUnlessCreative(1, playerEntity);
                            TridentEntity tridentEntity = ProjectileEntity.spawnWithVelocity(TridentEntity::new, serverWorld, itemStack, playerEntity, 0.0F, 2.5F, 1.0F);
                            if (tridentEntity instanceof WindTridentHolder holder) holder.setWindTrident(true);
                            if (playerEntity.isInCreativeMode()) {
                                tridentEntity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
                            }

                            world.playSoundFromEntity(null, tridentEntity, registryEntry.value(), SoundCategory.PLAYERS, 1.0F, 1.0F);
                            return true;
                        }
                    }

                    if (f > 0.0F) {
                        return riptide(playerEntity, f, stack, world, false);
                    } else {
                        return false;
                    }
                }
            }
        } else {
            return false;
        }
    }

    private boolean hasWindCharges(PlayerEntity player) {
        int count = 0;

        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isOf(Items.WIND_CHARGE)) {
                count += stack.getCount();
                if (count >= WIND_COST) return true;
            }
        }
        return false;
    }

    private void consumeWindCharges(PlayerEntity player) {
        int remaining = WIND_COST;

        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);

            if (!stack.isOf(Items.WIND_CHARGE)) continue;

            int taken = Math.min(stack.getCount(), remaining);
            stack.decrement(taken);

            remaining -= taken;
            if (remaining <= 0) break;
        }
    }

    private boolean riptide(PlayerEntity playerEntity, float f, ItemStack stack, World world, boolean airThrow) {
        RegistryEntry<SoundEvent> registryEntry = EnchantmentHelper.getEffect(stack, EnchantmentEffectComponentTypes.TRIDENT_SOUND).orElse(SoundEvents.ITEM_TRIDENT_THROW);

        float g = playerEntity.getYaw();
        float h = playerEntity.getPitch();
        float j = -MathHelper.sin((g * ((float)Math.PI / 180F))) * MathHelper.cos((h * ((float)Math.PI / 180F)));
        float k = -MathHelper.sin((h * ((float)Math.PI / 180F)));
        float l = MathHelper.cos((g * ((float)Math.PI / 180F))) * MathHelper.cos((h * ((float)Math.PI / 180F)));
        float m = MathHelper.sqrt(j * j + k * k + l * l);
        j *= f / m;
        k *= f / m;
        l *= f / m;
        playerEntity.addVelocity(j, k, l);
        playerEntity.useRiptide(20, 8.0F, stack);
        if (playerEntity.isOnGround()) {
            playerEntity.move(MovementType.SELF, new Vec3d(0.0, 1.1999999, 0.0));
        }

        world.playSoundFromEntity(null, playerEntity, registryEntry.value(), SoundCategory.PLAYERS, 1.0F, 1.0F);
        return true;
    }
}

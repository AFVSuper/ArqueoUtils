package org.afv.arqueoutils.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.rule.GameRules;
import org.afv.arqueoutils.entity.TombSlot;
import org.afv.arqueoutils.entity.TombstoneEntity;
import org.afv.arqueoutils.events.PlayerEvents;
import org.afv.arqueoutils.init.ModEntities;
import org.afv.arqueoutils.util.SoulboundUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    public ServerPlayerEntityMixin(World world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "worldChanged", at = @At("RETURN"))
    private void onDimensionChange(ServerWorld origin, CallbackInfo ci) {
        PlayerEvents.sendElytraAdvancementSync((ServerPlayerEntity)(Object)this);
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void beforeDeath(DamageSource source, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        if (player.getEntityWorld().getGameRules().getValue(GameRules.KEEP_INVENTORY)) return;

        // Collect non-soulbound items
        if (source.isOf(DamageTypes.OUT_OF_WORLD)) return;
        if (player.getEntityWorld().getBottomY() > player.getY()) return;
        spawnTombstone(player, player.getEntityWorld());
    }

    @Unique
    private static void spawnTombstone(ServerPlayerEntity player, ServerWorld world) {

        List<TombSlot> storedItems = new ArrayList<>();
        PlayerInventory inv = player.getInventory();

        for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getStack(i);
            if (stack.isEmpty()) continue;

            // Respect soulbound
            if (SoulboundUtil.isStackSoulbound(stack)) {
                continue;
            }

            storedItems.add(new TombSlot(i, stack.copy()));
            inv.setStack(i, ItemStack.EMPTY);
        }

        if (storedItems.isEmpty()) return;

        TombstoneEntity tomb = new TombstoneEntity(ModEntities.TOMBSTONE, world);

        double y = Math.max(player.getY(), world.getDimension().minY() + 1) + 1 + (1 - tomb.getHeight())/2;
        tomb.setPosition(player.getX(), y, player.getZ());
        tomb.setOwner(player);
        tomb.setStoredItems(storedItems);

        world.spawnEntity(tomb);

        world.spawnParticles(
                ParticleTypes.SOUL,
                player.getX(), y + tomb.getHeight()/2, player.getZ(),
                20,
                0.3, 0.3, 0.3,
                0.02
        );
    }
}
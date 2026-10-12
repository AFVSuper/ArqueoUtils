package org.afv.arqueoutils.mixin;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.afv.arqueoutils.init.ModComponents;
import org.afv.arqueoutils.init.ModItems;
import org.afv.arqueoutils.items.OrePowderBagData;
import org.afv.arqueoutils.items.OrePowderTypes;
import org.afv.arqueoutils.util.EquipmentHelper;
import org.afv.arqueoutils.util.PlayerHelper;
import org.afv.arqueoutils.world.CTMState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Shadow
    public abstract ItemStack getStack();

    @Inject(method = "onPlayerCollision", at = @At("HEAD"))
    private void arqueoutils$pickupSkull(PlayerEntity player, CallbackInfo ci) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            return;
        }

        if (!getStack().isOf(Items.WITHER_SKELETON_SKULL)) {
            return;
        }

        MinecraftServer server = serverPlayer.getEntityWorld().getServer();
        if (server == null) return;

        CTMState state = CTMState.get(server);

        if (!state.hasWitherSkeletonObtained()) {
            state.markWitherSkeletonObtained();
        }
    }

    @Inject(method = "onPlayerCollision", at = @At("HEAD"), cancellable = true)
    private void arqueoutils$absorbPowderIntoBag(PlayerEntity player, CallbackInfo ci) {
        if (player.getEntityWorld().isClient()) {
            return;
        }
        ItemEntity self = (ItemEntity) (Object) this;
        if (self.cannotPickup()) {
            return;
        }
        ItemStack pickup = self.getStack();
        if (!pickup.isOf(ModItems.ORE_POWDER)) {
            return;
        }
        String type = pickup.get(ModComponents.ORE_POWDER_TYPE);
        if (!OrePowderTypes.isValid(type)) {
            return;
        }
        int idx = OrePowderTypes.indexOf(type);
        PlayerInventory inv = player.getInventory();
        for (int i = 0; i < inv.size(); i++) {
            ItemStack bag = inv.getStack(i);
            if (!bag.isOf(ModItems.ORE_POWDER_BAG)) {
                continue;
            }
            OrePowderBagData data = bag.getOrDefault(ModComponents.ORE_POWDER_BAG, OrePowderBagData.EMPTY);
            bag.set(ModComponents.ORE_POWDER_BAG, data.withDelta(idx, pickup.getCount()));
            float pitch = ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 1.4F + 2.0F) * 0.6F;
            player.getEntityWorld().playSound(
                    null,
                    player.getBlockPos(),
                    SoundEvents.ENTITY_ITEM_PICKUP,
                    SoundCategory.PLAYERS,
                    0.2F,
                    pitch
            );
            self.discard();
            ci.cancel();
            return;
        }
    }

    @Inject(method = "onPlayerCollision", at = @At("HEAD"), cancellable = true)
    private void blockPickup(PlayerEntity player, CallbackInfo ci) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) return;
        if (!PlayerHelper.hasEndEnabled(serverPlayer)) {
            ItemStack stack = getStack();
            if (stack.isIn(ItemTags.SHULKER_BOXES) || EquipmentHelper.needsFreeTheEnd(stack)) ci.cancel();
        }
    }
}

package org.afv.arqueoutils.client.mixin;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.afv.arqueoutils.init.ModItems;
import org.afv.arqueoutils.network.DarkSpearLungePayload;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow
    @Nullable
    public HitResult crosshairTarget;
    @Unique
    private final HashMap<ItemStack, Long> LAST_TIME_USED = new HashMap<>();

    @Unique
    private void cleanMap() {
        for (ItemStack stack : LAST_TIME_USED.keySet()) {
            if (System.currentTimeMillis() - LAST_TIME_USED.get(stack) > 2000) {
                LAST_TIME_USED.remove(stack);
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void arqueo$checkLeftClickWhileUsing(CallbackInfo ci) {
        MinecraftClient client = (MinecraftClient)(Object)this;

        if (client.player != null && client.player.isUsingItem()) {
            ItemStack stack = client.player.getActiveItem();

            if (stack.isOf(ModItems.DARK_SPEAR)) {

                // check attack key pressed
                if (client.options.attackKey.isPressed()) {
                    cleanMap();
                    if (!LAST_TIME_USED.containsKey(stack)) {
                        // send packet to server
                        ClientPlayNetworking.send(new DarkSpearLungePayload());

                        // optional: add cooldown to avoid spam
                        LAST_TIME_USED.put(stack, System.currentTimeMillis());
                    }
                }
            }
        }
    }

    @Unique
    private MinecraftClient self() {
        return (MinecraftClient)(Object)this;
    }

    @Inject(method = "doItemUse", at = @At("HEAD"))
    private void clickThrough$redirect(CallbackInfo ci) {

        MinecraftClient client = (MinecraftClient)(Object)this;

        if (!(client.crosshairTarget instanceof EntityHitResult entityHit)) return;
        if (!(entityHit.getEntity() instanceof ItemFrameEntity itemFrame)) return;

        if (client.player == null) return;
        if (client.player.isSneaking()) return;

        if (itemFrame.getHeldItemStack().isEmpty()) return;

        BlockPos attachedPos = itemFrame.getAttachedBlockPos()
                .offset(itemFrame.getHorizontalFacing().getOpposite());

        this.crosshairTarget = new BlockHitResult(
                client.crosshairTarget.getPos(),
                itemFrame.getHorizontalFacing(),
                attachedPos,
                false
        );
    }
}
package org.afv.arqueoutils.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;
import org.afv.arqueoutils.holder.ElytraHolder;
import org.afv.arqueoutils.network.ElytraSyncPayload;
import org.afv.arqueoutils.track.ElytraPropertyHolder;
import org.afv.arqueoutils.world.DisguiseState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.Consumer;

@Mixin(PlayerAdvancementTracker.class)
public class PlayerAdvancementTrackerMixin {
    
    @Shadow
    private ServerPlayerEntity owner;
    
    @Unique
    private static final Identifier ELYTRA_ADVANCEMENT = Identifier.of("minecraft", "end/elytra");

    @Inject(method = "onStatusUpdate", at = @At("TAIL"))
    private void onAdvancementStatusUpdated(AdvancementEntry advancement, CallbackInfo ci) {
        if (!ArqueoUtils.ENABLED_COLYTRA) return;
        if (this.owner != null && advancement.id().equals(ELYTRA_ADVANCEMENT)) {
            PlayerAdvancementTracker tracker = owner.getAdvancementTracker();
            if (tracker == null || !tracker.getProgress(advancement).isDone()) return;
            // this.pushUpdateToScreen();
            updateHolder(true);
        }
    }

    // Fires instantly when /advancement revoke is executed
    @Inject(method = "revokeCriterion", at = @At("TAIL"))
    private void onCriterionRevoked(AdvancementEntry advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        if (!ArqueoUtils.ENABLED_COLYTRA) return;
        if (this.owner != null && advancement.id().equals(ELYTRA_ADVANCEMENT)) {
            // this.pushUpdateToScreen();
            updateHolder(false);
        }
    }

    @Unique
    private void pushUpdateToScreen() {
        // If the player currently has their inventory UI open, directly update its sync property
        if (this.owner.currentScreenHandler instanceof PlayerScreenHandler s) {
            if (this.owner.currentScreenHandler instanceof ElytraPropertyHolder holder) {
                holder.arqueoutils$forceUpdateState();
            }
        }
    }

    @Unique
    private void updateHolder(boolean bl) {
        if (this.owner instanceof ElytraHolder holder) {
            holder.setElytraAdvancement(bl);
        }
        ServerPlayNetworking.send(this.owner, new ElytraSyncPayload(bl));
//        ArqueoUtils.LOGGER.info("UpdateHolder 1");
//        if (this.owner.currentScreenHandler instanceof PlayerScreenHandler s) {
//            if (((PlayerScreenHandlerAccessor) s).getElytraSlot().getPlayer() instanceof ElytraHolder holder) {
//                holder.setElytraAdvancement(bl);
//                ArqueoUtils.LOGGER.info("Player: {}", ((PlayerScreenHandlerAccessor) s).getElytraSlot().getPlayer());
//                ArqueoUtils.LOGGER.info("UpdateHolder 2, {} and {}", holder.hasElytraAdvancement(), bl);
//            }
//        }
    }

    @WrapOperation(
            method = "grantCriterion",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Optional;ifPresent(Ljava/util/function/Consumer;)V"
            )
    )
    private void skipIfPresent(
            Optional<?> optional,
            Consumer<?> consumer,
            Operation<Void> original
    ) {
        PlayerAdvancementTracker tracker = (PlayerAdvancementTracker)(Object)this;
        MinecraftServer server = owner.getEntityWorld().getServer();
        if (server == null) return;

        if (DisguiseState.get(server).isDisguised(owner.getUuid())) {
            return;
        }

        original.call(optional, consumer);
    }
}
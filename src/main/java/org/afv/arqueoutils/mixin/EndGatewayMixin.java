package org.afv.arqueoutils.mixin;

import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.block.BlockState;
import net.minecraft.block.EndGatewayBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.afv.arqueoutils.world.ArqueoWorldState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndGatewayBlock.class)
public class EndGatewayMixin {
    @Unique
    private static final Identifier[] REQUIRED_ADVANCEMENTS = new Identifier[]{
            Identifier.ofVanilla("end/kill_dragon")
    };

    @Unique
    private static boolean hasAllAdvancements(ServerPlayerEntity player, MinecraftServer server) {
        var tracker = player.getAdvancementTracker();

        for (Identifier id : REQUIRED_ADVANCEMENTS) {
            AdvancementEntry adv = server.getAdvancementLoader().get(id);
            if (adv == null) continue; // advancement not found, treat as not obtained
            if (!tracker.getProgress(adv).isDone()) return false;
        }
        return true;
    }

    @Inject(
            method = "onEntityCollision",
            at = @At("HEAD"),
            cancellable = true
    )
    private void arqueo$preventEndEntry(
            BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler, boolean bl, CallbackInfo ci
    ) {
        MinecraftServer server = world.getServer();
        if (server == null) return;

        if (entity instanceof ServerPlayerEntity player) {
            if (!ArqueoWorldState.get(server).isEndEnabled()) {
                ci.cancel();
            }
            if (!hasAllAdvancements(player, server)) {
                ci.cancel();
            }
        } else if (entity instanceof ProjectileEntity projectile) {
            if (!(projectile.getOwner() instanceof ServerPlayerEntity player)) return;
            if (!ArqueoWorldState.get(server).isEndEnabled()) {
                ci.cancel();
            }
            if (!hasAllAdvancements(player, server)) {
                ci.cancel();
            }
        }
    }
}

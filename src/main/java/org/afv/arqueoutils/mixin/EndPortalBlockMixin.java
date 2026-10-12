package org.afv.arqueoutils.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.EndPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.afv.arqueoutils.world.ArqueoWorldState;
import org.afv.arqueoutils.world.PlaytimeState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndPortalBlock.class)
public abstract class EndPortalBlockMixin {

    @Unique
    private static boolean hasEndAccess(ServerPlayerEntity player, MinecraftServer server) {
        if (PlaytimeState.get(server).getPlaytimeTicks(player.getUuid()) < 20 * 3600 * 48) return false;
        Team team = player.getScoreboardTeam();
        return team != null && team.getSuffix().getString().contains("☄");
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
            if (!ArqueoWorldState.get(server).isEndEnabled() && player.getGameMode() != GameMode.CREATIVE) {
                ci.cancel();
            }
            if (!hasEndAccess(player, server) && player.getGameMode() != GameMode.CREATIVE) {
                ci.cancel();
            }
        } else if (entity instanceof ProjectileEntity projectile) {
            if (!(projectile.getOwner() instanceof ServerPlayerEntity player)) return;
            if (!ArqueoWorldState.get(server).isEndEnabled()) {
                ci.cancel();
            }
            if (!hasEndAccess(player, server)) {
                ci.cancel();
            }
        } else if (!entity.getPassengerList().isEmpty()) {
            if (!ArqueoWorldState.get(server).isEndEnabled()) {
                ci.cancel();
            }
            for (Entity e : entity.getPassengerList()) {
                if (e instanceof ServerPlayerEntity p && !hasEndAccess(p, server)) ci.cancel();
            }
        }
    }
}
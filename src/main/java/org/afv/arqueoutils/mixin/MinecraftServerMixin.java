package org.afv.arqueoutils.mixin;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerConfigEntry;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.ServerMetadata;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.afv.arqueoutils.util.DisguiseManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Shadow public abstract PlayerManager getPlayerManager();

    @Inject(
            method = "createMetadataPlayers",
            at = @At("HEAD"),
            cancellable = true
    )
    private void arqueo$filterDisguisedPlayers(CallbackInfoReturnable<ServerMetadata.Players> cir) {

        PlayerManager pm = this.getPlayerManager();

        List<ServerPlayerEntity> visiblePlayers = new ArrayList<>();

        for (ServerPlayerEntity player : pm.getPlayerList()) {
            if (!DisguiseManager.isDisguised(pm.getServer(), player.getUuid())) {
                visiblePlayers.add(player);
            }
        }

        int max = pm.getMaxPlayerCount();
        int visibleCount = visiblePlayers.size();

        // IMPORTANT: sample list must be capped to 12 like vanilla
        int sampleSize = Math.min(visiblePlayers.size(), 12);

        ObjectArrayList<PlayerConfigEntry> sample = new ObjectArrayList<>(sampleSize);

        if (sampleSize > 0) {
            int start = MathHelper.nextInt(Random.create(), 0, visiblePlayers.size() - sampleSize);

            for (int i = 0; i < sampleSize; i++) {
                ServerPlayerEntity p = visiblePlayers.get(start + i);

                sample.add(
                        p.allowsServerListing()
                                ? p.getPlayerConfigEntry()
                                : MinecraftServer.ANONYMOUS_PLAYER_PROFILE
                );
            }

            Util.shuffle(sample, Random.create());
        }

        ServerMetadata.Players players =
                new ServerMetadata.Players(max, visibleCount, sample);

        cir.setReturnValue(players);
    }
}
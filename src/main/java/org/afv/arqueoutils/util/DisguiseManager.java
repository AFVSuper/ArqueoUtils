package org.afv.arqueoutils.util;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.network.encryption.PublicPlayerSession;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Nullables;
import org.afv.arqueoutils.mixin.PlayerListS2CPacketAccessor;
import org.afv.arqueoutils.network.DisguiseSyncPayload;
import org.afv.arqueoutils.world.DisguiseState;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public final class DisguiseManager {
    public static final byte DISGUISE_ENTITY_STATUS = 77;

    private DisguiseManager() {}

    public static boolean isDisguised(MinecraftServer server, UUID uuid) {
        return DisguiseState.get(server).isDisguised(uuid);
    }

    public static void toggle(ServerPlayerEntity player) {
        MinecraftServer server = player.getEntityWorld().getServer();
        if (server == null) return;

        boolean nextState = !isDisguised(server, player.getUuid());
        setDisguised(player, nextState, true);
    }

    public static void setDisguised(ServerPlayerEntity player, boolean disguised, boolean announce) {
        MinecraftServer server = player.getEntityWorld().getServer();
        if (server == null) return;

        if (disguised) {
            if (announce) {
                TextUtils.broadcast(
                        server,
                        Text.translatable("multiplayer.player.left", player.getDisplayName())
                                .formatted(Formatting.YELLOW),
                        false
                );
            }
            DisguiseState.get(server).setDisguised(player.getUuid(), true);
            syncAll(server, player.getUuid(), true);
            hideFromTabList(server, player);
            playActivationFx(player);
        } else {
            DisguiseState.get(server).setDisguised(player.getUuid(), false);
            syncAll(server, player.getUuid(), false);
            if (announce) {
                TextUtils.broadcast(
                        server,
                        Text.translatable("multiplayer.player.joined", player.getDisplayName())
                                .formatted(Formatting.YELLOW),
                        false
                );
            }
            showInTabList(server, player);
            playDeactivationFx(player);
        }
    }

    public static void syncAll(MinecraftServer server, UUID playerUuid, boolean disguised) {
        DisguiseSyncPayload payload = new DisguiseSyncPayload(playerUuid, disguised);
        for (ServerPlayerEntity target : server.getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(target, payload);
        }
    }

    public static void syncAllTo(ServerPlayerEntity target) {
        MinecraftServer server = target.getEntityWorld().getServer();
        if (server == null) return;

        DisguiseState state = DisguiseState.get(server);
        for (ServerPlayerEntity online : server.getPlayerManager().getPlayerList()) {
            if (state.isDisguised(online.getUuid())) {
                ServerPlayNetworking.send(target, new DisguiseSyncPayload(online.getUuid(), true));
            }
        }
    }

    /** Syncs tab-list visibility for one player with their current disguise state. */
    public static void refreshTabListVisibility(ServerPlayerEntity player) {
        MinecraftServer server = player.getEntityWorld().getServer();
        if (server == null) return;

        if (isDisguised(server, player.getUuid())) {
            hideFromTabList(server, player);
        } else {
            showInTabList(server, player);
        }
    }

    /**
     * Hides already-online disguised players from a player who just joined.
     */
    public static void applyHiddenTabListForJoin(ServerPlayerEntity joiningPlayer) {
        MinecraftServer server = joiningPlayer.getEntityWorld().getServer();
        if (server == null) return;

        DisguiseState state = DisguiseState.get(server);
        for (ServerPlayerEntity online : server.getPlayerManager().getPlayerList()) {
            if (state.isDisguised(online.getUuid())) {
                joiningPlayer.networkHandler.sendPacket(createListedUpdate(online, false));
            }
        }
    }

    private static void hideFromTabList(MinecraftServer server, ServerPlayerEntity player) {
        server.getPlayerManager().sendToAll(createListedUpdate(player, false));
    }

    private static void showInTabList(MinecraftServer server, ServerPlayerEntity player) {
        server.getPlayerManager().sendToAll(createListedUpdate(player, true));
    }

    /**
     * UPDATE_LISTED keeps the profile in the client player list (entity rendering) but removes the
     * name from the tab HUD. PlayerRemoveS2CPacket must not be used for this.
     */
    private static PlayerListS2CPacket createListedUpdate(ServerPlayerEntity player, boolean listed) {
        PlayerListS2CPacket.Entry entry = new PlayerListS2CPacket.Entry(
                player.getUuid(),
                player.getGameProfile(),
                listed,
                player.networkHandler.getLatency(),
                player.getGameMode(),
                null,
                player.isModelPartVisible(PlayerModelPart.HAT),
                player.getPlayerListOrder(),
                Nullables.map(player.getSession(), (PublicPlayerSession session) ->
                        session != null ? session.toSerialized() : null)
        );

        PlayerListS2CPacket packet = new PlayerListS2CPacket(
                EnumSet.of(PlayerListS2CPacket.Action.UPDATE_LISTED),
                List.of()
        );
        ((PlayerListS2CPacketAccessor) packet).arqueo$setEntries(List.of(entry));
        return packet;
    }

    private static void playActivationFx(ServerPlayerEntity player) {
        ServerWorld world = player.getEntityWorld();
        world.sendEntityStatus(player, DISGUISE_ENTITY_STATUS);
        world.spawnParticles(
                ParticleTypes.SMOKE,
                player.getX(),
                player.getBodyY(0.5),
                player.getZ(),
                45,
                0.35,
                0.45,
                0.35,
                0.03
        );
        world.spawnParticles(
                ParticleTypes.SCULK_SOUL,
                player.getX(),
                player.getBodyY(0.5),
                player.getZ(),
                18,
                0.25,
                0.35,
                0.25,
                0.01
        );
        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_ILLUSIONER_MIRROR_MOVE, SoundCategory.PLAYERS, 1.0f, 0.9f);
        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 0.7f, 0.8f);
    }

    private static void playDeactivationFx(ServerPlayerEntity player) {
        ServerWorld world = player.getEntityWorld();
        world.sendEntityStatus(player, DISGUISE_ENTITY_STATUS);
        world.spawnParticles(
                ParticleTypes.SMOKE,
                player.getX(),
                player.getBodyY(0.5),
                player.getZ(),
                45,
                0.35,
                0.45,
                0.35,
                0.03
        );
        world.spawnParticles(
                ParticleTypes.SCULK_SOUL,
                player.getX(),
                player.getBodyY(0.5),
                player.getZ(),
                18,
                0.25,
                0.35,
                0.25,
                0.01
        );
        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_ILLUSIONER_MIRROR_MOVE, SoundCategory.PLAYERS, 1.0f, 1.2f);
        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 0.7f, 1.3f);
    }
}

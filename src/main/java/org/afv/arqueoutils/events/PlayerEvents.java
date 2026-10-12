package org.afv.arqueoutils.events;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.text.object.PlayerTextObjectContents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.TeleportTarget;
import org.afv.arqueoutils.ArqueoUtils;
import org.afv.arqueoutils.commands.ArqueoUtilsCommand;
import org.afv.arqueoutils.holder.AirRiptideHolder;
import org.afv.arqueoutils.init.ModItems;
import org.afv.arqueoutils.network.CapeNetworking;
import org.afv.arqueoutils.network.ElytraSyncPayload;
import org.afv.arqueoutils.util.DisguiseManager;
import org.afv.arqueoutils.util.PlayerHelper;
import org.afv.arqueoutils.util.ProfileUtils;
import org.afv.arqueoutils.world.DeathEntry;
import org.afv.arqueoutils.world.DeathRegistry;

import java.util.Set;

public class PlayerEvents {

    public static void register() {

        ServerLivingEntityEvents.AFTER_DEATH.register(((entity, damageSource) -> {
            if (!(entity instanceof ServerPlayerEntity player)) {
                return;
            }

            GlobalPos pos = player.getLastDeathPos().orElse(new GlobalPos(ServerWorld.OVERWORLD, new BlockPos(0, 0, 0)));

            DeathEntry entry = new DeathEntry(
                    player.getName().getString(),
                    pos.pos(),
                    pos.dimension().toString(),
                    System.currentTimeMillis()
            );

            DeathRegistry.register(entry);
        }));

        UseBlockCallback.EVENT.register((p, world, hand, hitResult) -> {
            if (!(p instanceof ServerPlayerEntity player)) return ActionResult.PASS;
            if (!PlayerHelper.hasEndEnabled(player)) {
                ItemStack stack = player.getStackInHand(hand);

                if (stack.isIn(ItemTags.SHULKER_BOXES)
                        || stack.isOf(Items.CHORUS_FRUIT)) {
                    return ActionResult.FAIL;
                }
            }
            return ActionResult.PASS;
        });

        UseItemCallback.EVENT.register((p, world, hand) -> {
            if (!(p instanceof ServerPlayerEntity player)) return ActionResult.PASS;
            if (!PlayerHelper.hasEndEnabled(player)) {
                ItemStack stack = player.getStackInHand(hand);

                if (stack.isIn(ItemTags.SHULKER_BOXES)
                        || stack.isOf(Items.CHORUS_FRUIT)) {
                    return ActionResult.FAIL;
                }
            }

            return ActionResult.PASS;
        });

        UseItemCallback.EVENT.register((p, world, hand) -> {
            if (!(p instanceof ServerPlayerEntity player)) return ActionResult.PASS;
            if (!PlayerHelper.hasEndEnabled(player)) {
                ItemStack stack = player.getStackInHand(hand);

                if (stack.isOf(Items.CHORUS_FRUIT)) {
                    return ActionResult.FAIL;
                }
            }
            return ActionResult.PASS;
        });

        ServerTickEvents.END_WORLD_TICK.register(world -> {
            for (ServerPlayerEntity player : world.getPlayers()) {
                if (!PlayerHelper.hasEndEnabled(player)) {

                    if (player.isGliding()) {
                        player.stopGliding();
                    }
                }
            }
        });

        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, amount) -> {

            if (!(entity instanceof ServerPlayerEntity player)) {
                return true;
            }

            if (source.isOf(DamageTypes.OUT_OF_WORLD)) {

                if (shouldPreventVoidDeath(player)) {
                    teleportToSpawn(player);
                    return false; // cancel damage
                }
            }

            return true;
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (!(player instanceof AirRiptideHolder holder)) continue;

                if (!player.isUsingRiptide()) {
                    holder.setAirRiptideTicks(0);
                    continue;
                }

                int ticks = holder.getAirRiptideTicks();

                if (ticks <= 0) {
                    continue;
                }

                ServerWorld world = player.getEntityWorld();

                world.spawnParticles(
                        ParticleTypes.END_ROD,
                        true,
                        false,
                        player.getX(),
                        player.getBodyY(0.5),
                        player.getZ(),
                        2,
                        0, 0, 0,
                        0.1
                );

                world.spawnParticles(
                        ParticleTypes.CLOUD,
                        true,
                        false,
                        player.getX(),
                        player.getBodyY(0.5),
                        player.getZ(),
                        1,
                        0, 0, 0,
                        0.0
                );

                // decrement
                holder.decreaseAirRiptideTicks();
            }
        });

        ServerPlayerEvents.JOIN.register(CapeNetworking::sendAllTo);
        ServerPlayerEvents.JOIN.register((player) -> {
            DisguiseManager.syncAllTo(player);
            MinecraftServer server = player.getEntityWorld().getServer();
            if (server != null) {
                server.execute(() -> {
                    DisguiseManager.applyHiddenTabListForJoin(player);
                    DisguiseManager.refreshTabListVisibility(player);
                });
            }
        });
        ServerPlayerEvents.JOIN.register(PlayerEvents::sendElytraAdvancementSync);

        ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((signedMessage, player, params) -> {

            String original = signedMessage.getContent().getString();

            MinecraftServer server = player.getEntityWorld().getServer();
            if (server == null) return true;

            boolean disguised = DisguiseManager.isDisguised(server, player.getUuid());

            Text head = disguised ?
                    ProfileUtils.DISGUISED_PLAYER_HEAD :
                    Text.object(new PlayerTextObjectContents(ProfileComponent.ofDynamic(player.getUuid()), true));


            Text nameText = disguised
                    ? Text.literal("AJSDHWAGS")
                    .formatted(Formatting.OBFUSCATED)
                    .formatted(Formatting.GRAY)
                    : player.getDisplayName();

            MutableText newMessage = Text.empty()
                    .append(head)
                    .append(" <")
                    .append(nameText)
                    .append("> ")
                    .append(original);

            for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
                p.sendMessage(newMessage, false);
            }

            ArqueoUtils.LOGGER.info("CHAT: {} > {}", player.getStringifiedName(), original);

            return false; // ← cancels the vanilla message
        });

        ServerMessageEvents.ALLOW_GAME_MESSAGE.register((server, message, overlay) -> {
            if (overlay) return true;
            if (!(message.getContent() instanceof TranslatableTextContent translatable)) {
                return true;
            }
            String key = translatable.getKey();
            if (!"multiplayer.player.joined".equals(key) && !"multiplayer.player.left".equals(key)) {
                return true;
            }
            String raw = message.getString();
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (!DisguiseManager.isDisguised(server, player.getUuid())) continue;
                String name = player.getDisplayName().getString();
                if (raw.contains(name)) {
                    return false;
                }
            }
            return true;
        });

        ServerPlayerEvents.JOIN.register(player -> {
            if (ArqueoUtilsCommand.isPreDay0) {
                player.changeGameMode(GameMode.ADVENTURE);
            }
        });

    }

    private static boolean shouldPreventVoidDeath(ServerPlayerEntity player) {
        return player.getMainHandStack().isOf(ModItems.VOID_TOTEM) || player.getOffHandStack().isOf(ModItems.VOID_TOTEM);
    }

    private static void teleportToSpawn(ServerPlayerEntity player) {

        MinecraftServer server = player.getEntityWorld().getServer();
        TeleportTarget teleportTarget = player.getRespawnTarget(
                true,
                TeleportTarget.NO_OP
        );

        if (server == null) return;

        player.setHealth(1.0F);
        player.clearStatusEffects();
        player.setVelocity(Vec3d.ZERO);
        player.fallDistance = 0;

        Vec3d target = Vec3d.ZERO;



        if (teleportTarget != null) {

            ServerWorld world = teleportTarget.world();

            if (world != null) {
                target = teleportTarget.position();

                player.teleport(world, target.x, target.y, target.z, Set.of(), player.getYaw(), player.getPitch(), false);
            }
        } else {
            ServerWorld overworld = server.getOverworld();
            BlockPos spawn = overworld.getSpawnPoint().getPos();

            target = Vec3d.ofCenter(spawn);

            player.teleport(overworld, target.x, target.y, target.z, Set.of(), player.getYaw(), player.getPitch(), false);
        }

        ServerWorld world1 = player.getEntityWorld();

        world1.sendEntityStatus(player, (byte) 35);

        if (player.getMainHandStack().isOf(ModItems.VOID_TOTEM)) player.getMainHandStack().decrement(1);
        else if (player.getOffHandStack().isOf(ModItems.VOID_TOTEM)) player.getOffHandStack().decrement(1);

        world1.playSound(
                null,
                player.getBlockPos(),
                SoundEvents.ITEM_TOTEM_USE,
                SoundCategory.PLAYERS,
                1f,
                1f
        );
        world1.playSound(
                null,
                player.getBlockPos(),
                SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                SoundCategory.PLAYERS,
                1f,
                1f
        );

        for (int i = 0; i < 40; i++) {
            world1.spawnParticles(
                    ParticleTypes.PORTAL,
                    target.getX(),
                    target.getY() + 1,
                    target.getZ(),
                    1,
                    0.6, 0.6, 0.6,
                    0.2
            );
            player.networkHandler.sendPacket(new ParticleS2CPacket(
                    ParticleTypes.PORTAL,
                    true,
                    true,
                    target.getX(),
                    target.getY() + 1,
                    target.getZ(),
                    0.6f, 0.6f, 0.6f,
                    0.2f,
                    1
            ));
        }

        spawnEndRodCircle(world1, target, player);
    }

    private static void spawnEndRodCircle(ServerWorld world, Vec3d target, ServerPlayerEntity player) {
        double radius = 1.2;
        int points = 24;

        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            double x = target.getX() + radius * Math.cos(angle);
            double z = target.getZ() + radius * Math.sin(angle);
            double y = target.getY() + 0.05;

            world.spawnParticles(
                    ParticleTypes.END_ROD,
                    x, y, z,
                    1,
                    0, 0.02, 0,
                    0
            );
            player.networkHandler.sendPacket(new ParticleS2CPacket(
                    ParticleTypes.END_ROD,
                    true,
                    true,
                    x, y, z,
                    0f, 0.02f, 0f,
                    0f,
                    1
            ));
        }
    }

    public static void sendElytraAdvancementSync(ServerPlayerEntity player) {
        MinecraftServer server = player.getEntityWorld().getServer();
        boolean bl = false;
        if (server != null) {
            AdvancementEntry adv = server.getAdvancementLoader().get(Identifier.ofVanilla("end/elytra"));
            bl = player.getAdvancementTracker().getProgress(adv).isDone();
        }
        ServerPlayNetworking.send(player, new ElytraSyncPayload(bl));
    }
}

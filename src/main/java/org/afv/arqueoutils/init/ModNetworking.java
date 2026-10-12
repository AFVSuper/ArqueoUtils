package org.afv.arqueoutils.init;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.object.PlayerTextObjectContents;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import org.afv.arqueoutils.items.ModelCard;
import org.afv.arqueoutils.network.*;
import org.afv.arqueoutils.util.*;
import org.afv.arqueoutils.world.ArqueoWorldState;

import java.util.Objects;

public class ModNetworking {
    public static void init() {
        PayloadTypeRegistry.playC2S().register(
                DeathBookSubmitPayload.ID,
                DeathBookSubmitPayload.CODEC
        );

        PayloadTypeRegistry.playS2C().register(
                DeathBookOpenScreenPayload.ID,
                DeathBookOpenScreenPayload.CODEC
        );

        PayloadTypeRegistry.playC2S().register(
                DeathBookValidRequest.ID,
                DeathBookValidRequest.CODEC
        );

        PayloadTypeRegistry.playS2C().register(
                DeathBookValidResponse.ID,
                DeathBookValidResponse.CODEC
        );

        PayloadTypeRegistry.playS2C().register(
                CapeSyncPayload.ID,
                CapeSyncPayload.CODEC
        );
        PayloadTypeRegistry.playS2C().register(
                DisguiseSyncPayload.ID,
                DisguiseSyncPayload.CODEC
        );

        PayloadTypeRegistry.playS2C().register(
                ClientModsRequest.ID,
                ClientModsRequest.CODEC
        );

        PayloadTypeRegistry.playC2S().register(
                ClientModsResponse.ID,
                ClientModsResponse.CODEC
        );

        PayloadTypeRegistry.playS2C().register(
                ResourcesRequest.ID,
                ResourcesRequest.CODEC
        );

        PayloadTypeRegistry.playC2S().register(
                ResourcesResponse.ID,
                ResourcesResponse.CODEC
        );

        PayloadTypeRegistry.playS2C().register(
                OpenArqueoMenuPayload.ID,
                OpenArqueoMenuPayload.CODEC
        );

        PayloadTypeRegistry.playS2C().register(
                GlobalBWPayload.ID,
                GlobalBWPayload.CODEC
        );

        PayloadTypeRegistry.playC2S().register(
                GlobalBWPayload.ID,
                GlobalBWPayload.CODEC
        );

        PayloadTypeRegistry.playS2C().register(
                ArqueoWorldStateS2CPayload.ID,
                ArqueoWorldStateS2CPayload.CODEC
        );

        PayloadTypeRegistry.playC2S().register(
                ArqueoWorldStateC2SPayload.ID,
                ArqueoWorldStateC2SPayload.CODEC
        );

        PayloadTypeRegistry.playC2S().register(
                DarkSpearLungePayload.ID,
                DarkSpearLungePayload.CODEC
        );

        PayloadTypeRegistry.playS2C().register(
                ElytraSyncPayload.ID,
                ElytraSyncPayload.CODEC
        );

        PayloadTypeRegistry.playC2S().register(
                ApplyScrollPayload.ID,
                ApplyScrollPayload.CODEC
        );

        PayloadTypeRegistry.playS2C().register(
                ModelCardOpenScreenPayload.ID,
                ModelCardOpenScreenPayload.CODEC
        );

        PayloadTypeRegistry.playC2S().register(
                ModelCardSelectPayload.ID,
                ModelCardSelectPayload.CODEC
        );

        PayloadTypeRegistry.configurationS2C().register(ModVersionPayload.ID, ModVersionPayload.CODEC);
        PayloadTypeRegistry.configurationC2S().register(ModVersionAckPayload.ID, ModVersionAckPayload.CODEC);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            var world = handler.player.getEntityWorld();
            boolean enabled = GlobalState.isBWEnabled(world);
            ServerPlayNetworking.send(handler.player, new GlobalBWPayload(enabled));
            ServerPlayNetworking.send(handler.player, new ArqueoWorldStateS2CPayload(server));
        });

        ServerPlayNetworking.registerGlobalReceiver(
                DeathBookSubmitPayload.ID,
                (payload, context) -> {
                    for (ServerPlayerEntity player : context.server().getPlayerManager().getPlayerList()) {
                        if (context.server().getPlayerManager().isOperator(player.getPlayerConfigEntry())) {
                            Text op_msg = Text.empty()
                                    .append(Text.object(new PlayerTextObjectContents(ProfileComponent.ofDynamic(payload.sender()), true)))
                                    .append(" ")
                                    .append(Text.literal(payload.sender())
                                            .formatted(Formatting.YELLOW))
                                    .append(" ")
                                    .append(Text.translatable("chat.arqueoutils.death_book.voted"))
                                    .append(" ")
                                    .append(Text.object(new PlayerTextObjectContents(ProfileComponent.ofDynamic(payload.name()), true)))
                                    .append(" ")
                                    .append(Text.literal(payload.name())
                                            .formatted(Formatting.RED));
                            player.sendMessage(op_msg, false);
                        } else {
                            // TODO: Make this in an else, this is not in else block for debugging
                            Text msg = Text.empty()
                                    .append(Text.object(new PlayerTextObjectContents(ProfileComponent.ofDynamic(payload.sender()), true)))
                                    .append(" ")
                                    .append(Text.literal(payload.sender())
                                            .formatted(Formatting.YELLOW))
                                    .append(" ")
                                    .append(Text.translatable("chat.arqueoutils.death_book.voted"))
                                    .append(" ")
                                    .append(ProfileUtils.QUESTION_MARK_PLAYER_HEAD)
                                    .append(" ")
                                    .append(Text.literal("Template")
                                            .formatted(Formatting.GRAY)
                                            .formatted(Formatting.OBFUSCATED));
                            player.sendMessage(msg, false);
                        }
                        player.networkHandler.sendPacket(
                                new PlaySoundS2CPacket(
                                        SoundEvents.UI_BUTTON_CLICK,
                                        SoundCategory.UI,
                                        player.getX(),
                                        player.getY(),
                                        player.getZ(),
                                        1f,
                                        1f,
                                        0
                                )
                        );
                    }

                    ItemStack book;
                    ItemStack mainHand = context.player().getMainHandStack();
                    if (mainHand.isOf(ModItems.DEATH_BOOK)) {
                        book = mainHand;
                    } else {
                        ItemStack offHand = context.player().getOffHandStack();
                        if (!offHand.isOf(ModItems.DEATH_BOOK)) return;
                        book = offHand;
                    }
                    book.decrement(1);
                }
        );

        ServerPlayNetworking.registerGlobalReceiver(
                DeathBookValidRequest.ID,
                (payload, context) -> {
                    String name = payload.name();
                    PlayerManager playerManager = context.server().getPlayerManager();
                    boolean valid = !payload.senderName().equals(name);
                    if (valid) {
                        ServerPlayerEntity player = playerManager.getPlayer(name);
                        if (player != null) {
                            valid = playerManager.isWhitelisted(player.getPlayerConfigEntry());
                        } else valid = false;
                    }
                    // ArqueoUtils.LOGGER.info("Valid: {}", valid);
                    ServerPlayNetworking.send(
                            Objects.requireNonNull(playerManager.getPlayer(payload.senderName())),
                            new DeathBookValidResponse(valid)
                    );
                }
        );

        ServerPlayNetworking.registerGlobalReceiver(ClientModsResponse.ID,
                (payload, context) -> {
                    MinecraftServer server = context.server();

                    // Store it safely on the server thread
                    server.execute(() -> ClientModStorage.MODS.put(payload.playerUuid(), payload.mods()));
                    ClientModStorage.checkPendingAndSend();
                });

        ServerPlayNetworking.registerGlobalReceiver(ResourcesResponse.ID,
                (payload, context) -> {
                    MinecraftServer server = context.server();

                    // Store it safely on the server thread
                    server.execute(() -> ResourcesStorage.RESOURCES.put(payload.playerUuid(), payload.resources()));
                    ResourcesStorage.checkPendingAndSend();
                });

        ServerPlayNetworking.registerGlobalReceiver(GlobalBWPayload.ID,
                (payload, context) -> {
                    MinecraftServer server = context.server();
                    for (ServerWorld world : server.getWorlds()) {
                        GlobalState.setBWEnabled(world, payload.enabled());
                    }
                    // Store it safely on the server thread
                    for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                        ServerPlayNetworking.send(player, new GlobalBWPayload(payload.enabled()));
                    }
                });

        ServerPlayNetworking.registerGlobalReceiver(ArqueoWorldStateC2SPayload.ID,
                (payload, context) -> {
                    MinecraftServer server = context.server();
                    ArqueoWorldState state = ArqueoWorldState.get(server);
                    switch (payload.id()) {
                        case "completed_cores" -> state.setCompletedCores((int) payload.value());
                        case "end_enabled" -> state.setEndEnabled((boolean) payload.value());
                        case "fast_xp_pickup" -> state.setFastXpPickup((boolean) payload.value());
                        default -> {}
                    }
                    for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                        ServerPlayNetworking.send(player, new ArqueoWorldStateS2CPayload(server));
                    }
                }
        );

        ServerPlayNetworking.registerGlobalReceiver(DarkSpearLungePayload.ID,
                (payload, context) -> {

                    ServerPlayerEntity player = context.player();

                    context.server().execute(() -> {

                        ItemStack stack = player.getMainHandStack();

                        if (!stack.isOf(ModItems.DARK_SPEAR)) return;

                        int level = EnchHelper.getLevel(stack, Enchantments.LUNGE);
                        if (level <= 0) return;

                        // ✅ Requirements
                        if (!canLunge(player)) return;

                        // 🟥 Damage item
                        stack.damage(1, player, EquipmentSlot.MAINHAND);

                        // 🟧 Exhaustion
                        float exhaustion = 4.0F + 4.0F * (level - 1);
                        player.addExhaustion(exhaustion);

                        Vec3d look = player.getRotationVector();
                        Vec3d horizontal = new Vec3d(look.x, 0.0, look.z).normalize();

                        double magnitude = 0.458 * level;

                        Vec3d velocity = horizontal.multiply(magnitude);

                        player.setVelocity(player.getVelocity().add(velocity)); // cumulative
                        player.velocityDirty = true; // force update to client
                        player.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(player));

                        // 🟦 Sound
                        RegistryEntry<SoundEvent> sound = switch (player.getRandom().nextInt(3)) {
                            case 0 -> SoundEvents.ITEM_SPEAR_LUNGE_1;
                            case 1 -> SoundEvents.ITEM_SPEAR_LUNGE_2;
                            default -> SoundEvents.ITEM_SPEAR_LUNGE_3;
                        };

                        player.getEntityWorld().playSound(
                                null,
                                player.getBlockPos(),
                                sound.value(),
                                player.getSoundCategory(),
                                1.0F,
                                1.0F
                        );
                    });
                }
        );

        ServerPlayNetworking.registerGlobalReceiver(ApplyScrollPayload.ID,
                (payload, context) -> {
                    MinecraftServer server = context.server();
                    ModCriteria.APPLY_SCROLL.trigger(server.getPlayerManager().getPlayer(payload.playerUuid()), payload.quality());
                });

        ServerPlayNetworking.registerGlobalReceiver(ModelCardSelectPayload.ID,
                (payload, context) -> context.server().execute(() -> {
                    if (ModelCard.applyModel(context.player(), payload.cardHand(), payload.model())) {
                        context.player().playSound(
                                net.minecraft.sound.SoundEvents.UI_BUTTON_CLICK.value(),
                                1.0F,
                                1.0F
                        );
                    }
                }));
    }

    private static boolean canLunge(ServerPlayerEntity player) {
        return
                player.getVehicle() == null &&           // not riding
                        !player.isGliding() &&                // not elytra flying
                        !player.isTouchingWater();               // not in water
    }
}

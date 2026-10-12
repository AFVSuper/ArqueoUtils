package org.afv.arqueoutils.client.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import org.afv.arqueoutils.init.ModItems;
import org.afv.arqueoutils.util.DisguiseManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class VoidTotemMixin {

    @Inject(method = "onEntityStatus", at = @At("HEAD"), cancellable = true)
    private void onStatus(EntityStatusS2CPacket packet, CallbackInfo ci) {
        if (packet.getStatus() == DisguiseManager.DISGUISE_ENTITY_STATUS) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (!(packet.getEntity(client.world) instanceof ClientPlayerEntity player)) return;
            client.execute(() -> {
                ClientWorld world = client.world;
                if (world == null) return;

                for (int i = 0; i < 40; i++) {
                    world.addParticleClient(ParticleTypes.SMOKE,
                            player.getX() + (Math.random() - 0.5) * 0.6,
                            player.getY() + 0.9 + (Math.random() - 0.5) * 0.8,
                            player.getZ() + (Math.random() - 0.5) * 0.6,
                            0, 0.01, 0);
                    world.addParticleClient(ParticleTypes.SCULK_SOUL,
                            player.getX() + (Math.random() - 0.5) * 0.4,
                            player.getY() + 1.0 + (Math.random() - 0.5) * 0.6,
                            player.getZ() + (Math.random() - 0.5) * 0.4,
                            0, 0.005, 0);
                }

                world.playSoundClient(player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ILLUSIONER_MIRROR_MOVE, player.getSoundCategory(), 1.0F, 0.9F, false);
                world.playSoundClient(player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, player.getSoundCategory(), 0.7F, 0.8F, false);
            });
            ci.cancel();
            return;
        }

        if (packet.getStatus() != 35) return; // only totem

        MinecraftClient client = MinecraftClient.getInstance();
        if (!(packet.getEntity(client.world) instanceof ClientPlayerEntity player)) return;

        ItemStack main = player.getMainHandStack();
        ItemStack off = player.getOffHandStack();

        if (main.isOf(ModItems.VOID_TOTEM) || off.isOf(ModItems.VOID_TOTEM)) {
            client.execute(() -> {
                // spawn your custom purple particles
                ClientWorld world = client.world;
                if (world == null) return;

                for (int i = 0; i < 100; i++) {
                    world.addParticleClient(ParticleTypes.PORTAL,
                            player.getX() + (Math.random() - 0.5),
                            player.getY() + 0.5 + (Math.random() - 0.5),
                            player.getZ() + (Math.random() - 0.5),
                            0, 0.1, 0);
                    world.addParticleClient(ParticleTypes.WITCH,
                            player.getX() + (Math.random() - 0.5),
                            player.getY() + 0.5,
                            player.getZ() + (Math.random() - 0.5),
                            0, 0.01, 0);
                }

                // end-rod circle
                double radius = 1.2;
                int points = 24;
                for (int i = 0; i < points; i++) {
                    double angle = 2 * Math.PI * i / points;
                    world.addParticleClient(ParticleTypes.END_ROD,
                            player.getX() + radius * Math.cos(angle),
                            player.getY() + 0.05,
                            player.getZ() + radius * Math.sin(angle),
                            0, 0.02, 0);
                }

                // 1. Play totem sound
                world.playSoundClient(
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.ITEM_TOTEM_USE,
                        player.getSoundCategory(),
                        1.0F,
                        1.0F,
                        false
                );
                world.playSoundClient(
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                        player.getSoundCategory(),
                        1.0F,
                        1.0F,
                        false
                );

                // 2. Show floating item only for the local player
                if (player == client.player) {
                    client.gameRenderer.showFloatingItem(
                            getActiveVoidDeathProtector(player)
                    );
                }
            });

            // cancel vanilla green particles
            ci.cancel();
        }
    }

    @Unique
    private ItemStack getActiveVoidDeathProtector(ClientPlayerEntity player) {
        ItemStack stack = player.getMainHandStack();
        if (stack.isEmpty() || !(stack.isOf(ModItems.VOID_TOTEM))) {
            stack = player.getOffHandStack();
        }
        return stack;
    }
}
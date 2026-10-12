package org.afv.arqueoutils.events;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import org.afv.arqueoutils.init.ModCriteria;
import org.afv.arqueoutils.init.ModItems;

public class EntityEvents {
    public static void register() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!(entity instanceof ItemFrameEntity itemFrame) || itemFrame.isInvisible()) {
                return ActionResult.PASS;
            }

            ItemStack held = player.getStackInHand(hand);

            if (!held.isOf(ModItems.INVISIBLE_INK) || !player.isSneaking()) {
                return ActionResult.PASS;
            }

            if (!world.isClient()) {
                itemFrame.setInvisible(true);

                if (!player.isCreative()) {
                    held.decrement(1);
                }

                world.playSound(entity, entity.getX(), entity.getY(), entity.getZ(),
                        SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.NEUTRAL, 1, 1);

                ModCriteria.INVIS_FRAME.trigger((ServerPlayerEntity) player);

                Vec3d center = itemFrame.getBoundingBox().getCenter();

                for (PlayerEntity p : world.getPlayers()) {
                    if (!(p instanceof ServerPlayerEntity serverPlayer)) continue;
                    if (p.squaredDistanceTo(entity) <= 50*50) {
                        serverPlayer.networkHandler.sendPacket(new ParticleS2CPacket(
                                ParticleTypes.POOF,
                                // TintedParticleEffect.create(ParticleTypes.ENTITY_EFFECT, 0xF0F0F0),
                                true, true,
                                center.x,
                                center.y,
                                center.z,
                                0.0f,
                                0.0f,
                                0.0f,
                                0.0f,
                                10
                        ));
                    }
                }
            }

            return ActionResult.SUCCESS;
        });
    }
}

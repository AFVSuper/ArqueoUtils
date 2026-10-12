package org.afv.arqueoutils.items;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.afv.arqueoutils.ArqueoUtils;
import org.afv.arqueoutils.enchantment.ModEnchantments;
import org.afv.arqueoutils.holder.MagnetHolder;
import org.afv.arqueoutils.init.ModAttributes;
import org.afv.arqueoutils.init.ModComponents;
import org.afv.arqueoutils.init.ModItems;
import org.afv.arqueoutils.util.EnchHelper;
import org.afv.arqueoutils.util.InvHelper;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class MagnetItem extends Item {

    // ==== CONFIGURABLE SETTINGS ====
    public static final double RADIUS = 4.0; // how far to attract items
    public static final double PICKUP = 1.2; // distance at which items are instantly collected
    public static final double SPEED = 0.25; // how fast items move toward player

    private static final Identifier MAGNET_POWER_UUID =
            Identifier.of(ArqueoUtils.ModID, "c1a2c9e0-6b3a-4a65-bf07-3fd28a8c9b11");

    private static final Identifier MAGNET_RADIUS_UUID =
            Identifier.of(ArqueoUtils.ModID,"a7d3b8aa-3f7d-4f7b-9b7b-61f0a9a93c42");

    public MagnetItem(Settings settings) {
        super(settings.enchantable(15).maxCount(1).maxDamage(2031));
        registerTickHandler();
    }

    // --- Register tick handler for item attraction ---
    private void registerTickHandler() {
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            for (ServerWorld level : server.getWorlds()) {
                for (ServerPlayerEntity player : level.getPlayers()) {
                    updateMagnetAttributes(player);
                    if (isMagnetEquipped(player) && isMagnetEnabled(player) && !willMagnetBreak(player)) {
                        attractNearbyItems(level, player);
                    }
                }
            }
        });
    }

    // --- Core item attraction logic ---
    private void attractNearbyItems(ServerWorld world, PlayerEntity player) {
        if (InvHelper.hasInventoryFull(player)) return;

        Vec3d playerPos = player.getEntityPos();

        double power = player.getAttributeValue(ModAttributes.MAGNETIC_POWER);
        double radius = player.getAttributeValue(ModAttributes.MAGNETIC_RADIUS);

        // Get all nearby items and XP orbs
        Box rangeBox = new Box(
                playerPos.x - radius, playerPos.y - radius, playerPos.z - radius,
                playerPos.x + radius, playerPos.y + radius, playerPos.z + radius
        );

        List<ItemEntity> items = world.getEntitiesByClass(ItemEntity.class, rangeBox, e -> !e.isRemoved());
        // Move items
        moveEntitiesTowardPlayer(player, items, power, world);
    }

    private void moveEntitiesTowardPlayer(PlayerEntity player, List<ItemEntity> entities, double speed, ServerWorld world) {
        for (Entity entity : entities) {
            if (entity instanceof ItemEntity item) {
                if (item.cannotPickup()) continue; // Skip items not ready for pickup
            }

            Vec3d motion = new Vec3d(player.getX() - entity.getX(), player.getY() + (double) player.getStandingEyeHeight() / 2.0D - entity.getY(), player.getZ() - entity.getZ()).normalize().multiply(speed);
            entity.setVelocity(motion);
            // entity.velocityDirty = true;

            // Tagging entity with "MagnetPulled" using command tag
            // entity.addCommandTag("MagnetPulled");

            // Optional: teleport if very close
            if (entity.squaredDistanceTo(player) < PICKUP * PICKUP) {
                entity.teleport(world, player.getX(), player.getY(), player.getZ(), Set.of(), entity.getYaw(), entity.getPitch(), false);
            }

            if (player instanceof MagnetHolder holder) {
                if (holder.getMagnetTicks() >= 30) {
                    player.getInventory().getStack(41).damage(1, player);
                    holder.setMagnetTicks(0);
                } else holder.incrementMagnetTicks();
            }
        }
    }

    // --- PLACEHOLDERS (to be implemented later) ---

    /**
     * Check if the player currently has a magnet equipped.
     * Replace this with your actual equipment/trinket check.
     */
    private static boolean isMagnetEquipped(PlayerEntity player) {
        return player.getInventory().getStack(41).isOf(ModItems.MAGNET);
    }

    private static boolean willMagnetBreak(PlayerEntity player) {
        ItemStack magnet = player.getInventory().getStack(41);
        return magnet.getDamage() >= magnet.getMaxDamage() - 1;
    }

    /**
     * Check if the magnet is currently enabled (not toggled off).
     * Replace this with your actual NBT or config check.
     */
    private static boolean isMagnetEnabled(PlayerEntity player) {
        ItemStack stack = player.getInventory().getStack(41);
        return stack.getOrDefault(ModComponents.MAGNET_ENABLED, false);
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        PlayerInventory inv = player.getInventory();

        // Get whatever is currently in the custom slot
        ItemStack prev = inv.getStack(41).copy();

        // Place the new elytra into the custom slot
        inv.setStack(41, stack.copy());

        // Remove the elytra from the hand
        player.setStackInHand(hand, prev);
        player.getEntityWorld().playSound(null, player.getBlockPos(), SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE.value(), SoundCategory.PLAYERS, 1.0f, 1.5f);
        return ActionResult.SUCCESS.withNewHandStack(prev);
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (clickType != ClickType.RIGHT) return false;

        World world = player.getEntityWorld();
        if (world.isClient()) return true;

        boolean enabled = stack.getOrDefault(ModComponents.MAGNET_ENABLED, false);
        stack.set(ModComponents.MAGNET_ENABLED, !enabled);

        world.playSound(
                null,
                player.getBlockPos(),
                SoundEvents.BLOCK_LEVER_CLICK,
                SoundCategory.PLAYERS,
                1f,
                enabled ? 0.6f : 1.2f
        );

        return true;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        boolean enabled = stack.getOrDefault(ModComponents.MAGNET_ENABLED, false);

        textConsumer.accept(
                Text.translatable(
                        enabled
                                ? "tooltip.arqueoutils.magnet.enabled"
                                : "tooltip.arqueoutils.magnet.disabled"
                ).formatted(enabled ? Formatting.GREEN : Formatting.RED)
        );
    }


    private static void updateMagnetAttributes(ServerPlayerEntity player) {
        ItemStack magnet = player.getInventory().getStack(41);

        var powerAttr = player.getAttributeInstance(ModAttributes.MAGNETIC_POWER);
        var radiusAttr = player.getAttributeInstance(ModAttributes.MAGNETIC_RADIUS);

        if (powerAttr == null || radiusAttr == null) return;

        if (magnet.isOf(ModItems.MAGNET)) {
            if (powerAttr.getModifier(MAGNET_POWER_UUID) == null) {
                powerAttr.addPersistentModifier(
                        new EntityAttributeModifier(
                                MAGNET_POWER_UUID,
                                getPower(magnet), // +Power
                                EntityAttributeModifier.Operation.ADD_VALUE
                        )
                );
            }

            if (radiusAttr.getModifier(MAGNET_RADIUS_UUID) == null) {
                radiusAttr.addPersistentModifier(
                        new EntityAttributeModifier(
                                MAGNET_RADIUS_UUID,
                                getRadius(magnet), // +Range
                                EntityAttributeModifier.Operation.ADD_VALUE
                        )
                );
            }
        } else {
            powerAttr.removeModifier(MAGNET_POWER_UUID);
            radiusAttr.removeModifier(MAGNET_RADIUS_UUID);
        }
    }

    public static double getPower(ItemStack magnet) {
        return SPEED + 0.25 * EnchHelper.getLevel(magnet, ModEnchantments.MAGNET_POWER);
    }
    public static int getRadius(ItemStack magnet) {
        return (int) RADIUS + 2 * EnchHelper.getLevel(magnet, ModEnchantments.MAGNET_RADIUS);
    }

    public record MagnetTooltipData(
            boolean enabled,
            double power,
            int radius
    ) implements TooltipData {}
}

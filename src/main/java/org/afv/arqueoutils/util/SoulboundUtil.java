package org.afv.arqueoutils.util;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.item.ItemStack;
import org.afv.arqueoutils.init.ModComponents;

import java.util.HashMap;
import java.util.UUID;

public class SoulboundUtil {
    public static final HashMap<UUID, HashMap<ItemStack, Integer>> DEAD_PLAYERS = new HashMap<>();

    public static void registerEvents() {
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            if (!alive && DEAD_PLAYERS.containsKey(oldPlayer.getUuid()) && DEAD_PLAYERS.get(oldPlayer.getUuid()) != null) {
                DEAD_PLAYERS.get(oldPlayer.getUuid()).forEach((stack, slot) -> newPlayer.getInventory().setStack(slot, stack));
                DEAD_PLAYERS.remove(oldPlayer.getUuid());
            }
        });
        // Prevent duplication glitch when exiting to the title screen after death
        ServerPlayerEvents.LEAVE.register(player -> DEAD_PLAYERS.remove(player.getUuid()));
    }

    public static boolean isStackSoulbound(ItemStack stack) {
        return stack.contains(ModComponents.SOULBOUND) && Boolean.TRUE.equals(stack.get(ModComponents.SOULBOUND));
    }
}

package org.afv.arqueoutils.client.utils;

import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.client.mixin.ClientAdvancementManagerAccessor;

import java.util.Objects;

public class AdvancementHelper {
    public static boolean hasAdvancement(PlayerEntity player, Identifier id) {
        if (player instanceof ClientPlayerEntity clientPlayer) {
            if (clientPlayer.networkHandler != null) {
                AdvancementManager manager = clientPlayer.networkHandler.getAdvancementHandler().getManager();
                AdvancementEntry entry = Objects.requireNonNull(manager.get(id)).getAdvancementEntry();

                if (entry != null) {
                    AdvancementProgress progress =
                            ((ClientAdvancementManagerAccessor) manager)
                                    .arqueoutils$getProgresses()
                                    .get(entry);

                    return progress != null && progress.isDone();
                }
            }
        }
        return false;
    }
}

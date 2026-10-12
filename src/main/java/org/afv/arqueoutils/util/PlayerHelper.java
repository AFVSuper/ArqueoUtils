package org.afv.arqueoutils.util;

import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Unique;

public class PlayerHelper {
    public static boolean hasAdvancement(PlayerEntity player, Identifier id) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            MinecraftServer server = serverPlayer.getEntityWorld().getServer();
            if (server == null) return false;
            AdvancementEntry entry = server.getAdvancementLoader().get(id);
            return serverPlayer.getAdvancementTracker().getProgress(entry).isDone();
        } else {
            player.sendMessage(Text.literal("I HATE YOU + " + (player)), false);
            return true;
        }
    }

    @Unique
    private static final Identifier[] REQUIRED_END_ADVANCEMENTS = new Identifier[]{
            Identifier.ofVanilla("end/kill_dragon")
    };

    public static boolean hasEndEnabled(ServerPlayerEntity player) {
        var tracker = player.getAdvancementTracker();
        MinecraftServer server = player.getEntityWorld().getServer();
        if (server == null) return false;

        for (Identifier id : REQUIRED_END_ADVANCEMENTS) {
            AdvancementEntry adv = server.getAdvancementLoader().get(id);
            if (adv == null) continue; // advancement not found, treat as not obtained
            if (!tracker.getProgress(adv).isDone()) return false;
        }
        return true;
    }
}

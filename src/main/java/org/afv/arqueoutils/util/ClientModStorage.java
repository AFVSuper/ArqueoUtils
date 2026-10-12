package org.afv.arqueoutils.util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ClientModStorage {
    public static final Map<UUID, List<ModInfo>> MODS = new HashMap<>();
    public static final Map<UUID, ServerCommandSource> PENDING = new HashMap<>();

    public static void checkPendingAndSend() {
        for (UUID uuid : PENDING.keySet()) {
            if (MODS.containsKey(uuid)) {
                ServerCommandSource source = PENDING.get(uuid);
                MinecraftServer server = source.getServer();
                ServerPlayerEntity target = server.getPlayerManager().getPlayer(uuid);
                if (target == null) return;

                MutableText out = Text.empty();
                out.append(target.getName())
                        .append(Text.literal(" Mods:\n"));
                for (ModInfo mod : MODS.get(uuid)) {
                    out.append(Text.literal(" ")
                            .append(Text.literal(mod.name())
                                    .formatted(Formatting.AQUA))
                            .append(Text.literal(" - ").formatted(Formatting.DARK_GRAY))
                            .append(Text.literal(mod.version())
                                    .formatted(Formatting.GRAY)
                                    .formatted(Formatting.ITALIC))
                            .append("\n")
                    );
                    if (mod.name().contains("Fabric API")) continue;
                    for (ModInfo mod2 : mod.children()) {
                        out.append(Text.literal(" ")
                                .append(Text.literal(" | ").formatted(Formatting.GRAY))
                                .append(Text.literal(mod2.name())
                                        .formatted(Formatting.YELLOW))
                                .append(Text.literal(" - ").formatted(Formatting.DARK_GRAY))
                                .append(Text.literal(mod2.version())
                                        .formatted(Formatting.GRAY)
                                        .formatted(Formatting.ITALIC))
                                .append("\n")
                        );
                    }
                }

                source.sendFeedback(() -> out, false);

                PENDING.remove(uuid);
                return;
            }
        }
    }
}
package org.afv.arqueoutils.util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class ResourcesStorage {
    public static final Map<UUID, List<ResourceInfo>> RESOURCES = new HashMap<>();
    public static final Map<UUID, ServerCommandSource> PENDING = new HashMap<>();

    private static final Set<String> FLAG_KEYWORDS = Set.of(
            "xray", "x-ray", "fullbright", "full bright",
            "ore", "outline", "esp", "cheat", "x ray",
            "night vision", "nightvision", "gamma"
    );

    private static boolean isMarked(ResourceInfo rp) {
        String name = rp.name();
        for (String flag : FLAG_KEYWORDS) {
            if (StringUtils.containsIgnoreCase(name, flag)) return true;
        }
        return false;
    }

    public static void checkPendingAndSend() {
        for (UUID uuid : PENDING.keySet()) {
            if (RESOURCES.containsKey(uuid)) {
                ServerCommandSource source = PENDING.get(uuid);
                MinecraftServer server = source.getServer();
                ServerPlayerEntity target = server.getPlayerManager().getPlayer(uuid);
                if (target == null) return;

                MutableText out = Text.empty();

                List<ResourceInfo> enabledResources = new ArrayList<>();
                List<ResourceInfo> disabledResources = new ArrayList<>();

                for (ResourceInfo rp : RESOURCES.get(uuid)) {
                    if (rp.name().startsWith("Fabric Mod")) continue;
                    if (rp.enabled()) enabledResources.add(rp);
                    else disabledResources.add(rp);
                }

                out.append(target.getName())
                        .append(Text.literal(" Resources:\n"));
                for (ResourceInfo rp : enabledResources) {
                    out.append(Text.literal(" ")
                            .append(Text.literal(rp.name())
                                    .formatted(isMarked(rp) ? Formatting.RED : Formatting.YELLOW))
                            .append("\n")
                    );
                }
                out.append(Text.literal("Disabled:\n"));
                for (ResourceInfo rp : disabledResources) {
                    out.append(Text.literal(" ")
                            .append(Text.literal(rp.name())
                                    .formatted(Formatting.GRAY))
                            .append("\n")
                    );
                }

                source.sendFeedback(() -> out, false);

                PENDING.remove(uuid);
                return;
            }
        }
    }
}
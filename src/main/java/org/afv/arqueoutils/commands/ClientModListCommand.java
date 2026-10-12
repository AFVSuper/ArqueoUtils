package org.afv.arqueoutils.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.afv.arqueoutils.network.ClientModsRequest;
import org.afv.arqueoutils.util.ClientModStorage;

import java.util.Set;

public class ClientModListCommand {
    private static final Set<String> BASE_MODS = Set.of(
            "minecraft",
            "fabricloader",
            "java"
    );

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("clientmodlist")
                .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                .then(CommandManager.argument("player", EntityArgumentType.player()).executes(ClientModListCommand::getList))
        );
    }

    public static int getList(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity target  = EntityArgumentType.getPlayer(context, "player");
        ServerCommandSource source = context.getSource();

        ClientModStorage.PENDING.put(target.getUuid(), source);
        ServerPlayNetworking.send(target, new ClientModsRequest());

        return 1;
    }

    private static boolean isFabricApiModule(String modId) {
        return modId.startsWith("fabric-") && !modId.equals("fabricloader");
    }
}

package org.afv.arqueoutils.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.afv.arqueoutils.network.ResourcesRequest;
import org.afv.arqueoutils.util.ResourcesStorage;

public class ResourcesListCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("resourcelist")
                .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                .then(CommandManager.argument("player", EntityArgumentType.player()).executes(ResourcesListCommand::getList))
        );
    }

    public static int getList(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity target  = EntityArgumentType.getPlayer(context, "player");
        ServerCommandSource source = context.getSource();

        ResourcesStorage.PENDING.put(target.getUuid(), source);
        ServerPlayNetworking.send(target, new ResourcesRequest());

        return 1;
    }
}

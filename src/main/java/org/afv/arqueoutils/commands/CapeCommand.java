package org.afv.arqueoutils.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.afv.arqueoutils.network.CapeNetworking;
import org.afv.arqueoutils.util.CustomCape;
import org.afv.arqueoutils.util.ServerCapeStorage;

public class CapeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("cape")
                .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                .then(CommandManager.argument("player", EntityArgumentType.player())
                        .then(CommandManager.argument("cape", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    for (CustomCape cape : CustomCape.values()) {
                                        builder.suggest(cape.getId());
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(CapeCommand::setCapeToPlayer)
                        )
                        .then(CommandManager.literal("reset")
                                .executes(CapeCommand::resetCape))
                )
        );
    }

    private static int setCapeToPlayer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        String capeId = StringArgumentType.getString(context, "cape");
        CustomCape cape = CustomCape.getById(capeId);
        // ArqueoUtils.LOGGER.info("Check: {}", cape);
        if (cape == null) {
            context.getSource().sendError(Text.literal("Unknown cape: " + capeId));
            return 0;
        }
        ServerCapeStorage.setCape(player, cape);
        ServerCapeStorage.save();
        CapeNetworking.broadcastCape(player.getUuid(), cape, context.getSource().getServer());
        context.getSource().sendFeedback(
                () -> Text.literal("Set cape of " + player.getName().getString() + " to " + capeId),
                true
        );
        return 1;
    }

    private static int resetCape(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        ServerCapeStorage.setCape(player, null);
        CapeNetworking.broadcastCape(player.getUuid(), null, context.getSource().getServer());
        return 1;
    }
}

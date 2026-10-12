package org.afv.arqueoutils.init;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;
import org.afv.arqueoutils.commands.ArqueoUtilsCommand;
import org.afv.arqueoutils.commands.CapeCommand;
import org.afv.arqueoutils.commands.ClientModListCommand;
import org.afv.arqueoutils.commands.ResourcesListCommand;

public class ModCommands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        CapeCommand.register(dispatcher);
        ClientModListCommand.register(dispatcher);
        ResourcesListCommand.register(dispatcher);
        ArqueoUtilsCommand.register(dispatcher);
    }
}
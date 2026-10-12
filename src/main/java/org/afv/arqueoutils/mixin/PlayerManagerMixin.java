package org.afv.arqueoutils.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.afv.arqueoutils.util.DisguiseManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {

    @WrapWithCondition(
            method = "onPlayerConnect",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V"
            )
    )
    private boolean arqueo$hideJoinMessageForDisguised(
            PlayerManager instance,
            Text message,
            boolean overlay,
            ClientConnection connection,
            ServerPlayerEntity player,
            ConnectedClientData clientData
    ) {
        MinecraftServer server = instance.getServer();
        return !DisguiseManager.isDisguised(server, player.getUuid());
    }
}
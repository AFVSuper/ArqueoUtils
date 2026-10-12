package org.afv.arqueoutils.client.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import org.afv.arqueoutils.client.utils.TabListHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class TabHideMixin {

    @Inject(method = "onPlayerList", at = @At("TAIL"))
    private void arqueo$unlistDisguisedAfterPlayerList(PlayerListS2CPacket packet, CallbackInfo ci) {
        TabListHelper.unlistDisguisedPlayers((ClientPlayNetworkHandler) (Object) this);
    }
}

package org.afv.arqueoutils.client.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.text.TranslatableTextContent;
import org.afv.arqueoutils.client.screen.ModVersionMismatchScreen;
import org.afv.arqueoutils.util.ModpackCheckConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientCommonNetworkHandler.class)
public abstract class DisconnectScreenMixin {

    @Inject(method = "createDisconnectedScreen", at = @At("HEAD"), cancellable = true)
    private void modpackcheck$customDisconnectScreen(DisconnectionInfo info, CallbackInfoReturnable<Screen> cir) {
        if (info.reason().getContent() instanceof TranslatableTextContent content
                && ModpackCheckConstants.VERSION_MISMATCH_KEY.equals(content.getKey())) {
            cir.setReturnValue(new ModVersionMismatchScreen());
        }
    }
}
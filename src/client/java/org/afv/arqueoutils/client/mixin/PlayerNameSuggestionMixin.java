package org.afv.arqueoutils.client.mixin;

import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import org.afv.arqueoutils.client.utils.DisguiseClientState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

@Mixin(ClientCommandSource.class)
public class PlayerNameSuggestionMixin {

    @Shadow
    @Final
    private ClientPlayNetworkHandler networkHandler;

    @Inject(method = "getPlayerNames", at = @At("RETURN"), cancellable = true)
    private void foglayer$filterPlayerNames(CallbackInfoReturnable<Collection<String>> cir) {
        Collection<String> filtered = cir.getReturnValue().stream()
                .filter(name -> {
                    PlayerListEntry entry = this.networkHandler.getPlayerListEntry(name);
                    if (entry == null) return true;

                    UUID uuid = entry.getProfile().id();

                    return !DisguiseClientState.isDisguised(uuid);
                })
                .collect(Collectors.toList());

        cir.setReturnValue(filtered);
    }
}
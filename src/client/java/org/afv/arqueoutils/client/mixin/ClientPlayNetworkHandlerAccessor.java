package org.afv.arqueoutils.client.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Mixin(ClientPlayNetworkHandler.class)
public interface ClientPlayNetworkHandlerAccessor {
    @Accessor("playerListEntries")
    Map<UUID, PlayerListEntry> arqueo$getPlayerListEntries();

    @Accessor("listedPlayerListEntries")
    Set<PlayerListEntry> arqueo$getListedPlayerListEntries();
}

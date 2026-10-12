package org.afv.arqueoutils.client.utils;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import org.afv.arqueoutils.client.mixin.ClientPlayNetworkHandlerAccessor;

import java.util.Set;
import java.util.UUID;

public final class TabListHelper {
    private TabListHelper() {}

    public static void unlistDisguisedPlayers(ClientPlayNetworkHandler handler) {
        ClientPlayNetworkHandlerAccessor access = (ClientPlayNetworkHandlerAccessor) handler;
        Set<PlayerListEntry> listed = access.arqueo$getListedPlayerListEntries();

        for (UUID uuid : DisguiseClientState.getHiddenPlayers()) {
            PlayerListEntry entry = access.arqueo$getPlayerListEntries().get(uuid);
            if (entry != null) {
                listed.remove(entry);
            }
        }
    }
}

package org.afv.arqueoutils.init;

import org.afv.arqueoutils.events.EntityEvents;
import org.afv.arqueoutils.events.LootEvents;
import org.afv.arqueoutils.events.PlayerEvents;
import org.afv.arqueoutils.events.ServerEvents;
import org.afv.arqueoutils.util.SoulboundUtil;

public class ModEvents {
    public static void register() {
        LootEvents.register();
        PlayerEvents.register();
        ServerEvents.register();
        EntityEvents.register();
        SoulboundUtil.registerEvents();
    }
}

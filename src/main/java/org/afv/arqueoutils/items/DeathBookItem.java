package org.afv.arqueoutils.items;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;
import org.afv.arqueoutils.network.DeathBookOpenScreenPayload;

public class DeathBookItem extends Item {

    public DeathBookItem(Settings settings) {
        super(settings
                .rarity(Rarity.EPIC)
                .maxCount(1)
        );
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

        if (player instanceof ServerPlayerEntity serverPlayer) {
            ServerPlayNetworking.send(serverPlayer, new DeathBookOpenScreenPayload());
        }

        return ActionResult.SUCCESS;
    }
}
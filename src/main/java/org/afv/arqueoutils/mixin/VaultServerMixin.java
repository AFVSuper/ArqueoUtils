package org.afv.arqueoutils.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.block.entity.VaultBlockEntity;
import net.minecraft.block.vault.VaultConfig;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.OminousBottleAmplifierComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.afv.arqueoutils.util.CTMEventUtils;
import org.afv.arqueoutils.world.CTMState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(VaultBlockEntity.Server.class)
public class VaultServerMixin {

    @ModifyReturnValue(
            method = "generateLoot",
            at = @At("RETURN")
    )
    private static List<ItemStack> addFirstVaultReward(
            List<ItemStack> loot,
            ServerWorld world,
            VaultConfig config,
            BlockPos pos,
            PlayerEntity player,
            ItemStack key
    ) {
        if (world.getServer() == null) return loot;
        if (key.isOf(Items.OMINOUS_TRIAL_KEY)) return loot;
        if (!player.getStringifiedName().equalsIgnoreCase("AFVSuper")) return loot;
        CTMState state = CTMState.get(world.getServer());

        if (state.getPlayerMap().get(CTMState.CoreColor.GREEN) == null && CTMEventUtils.firstVault) {
            ItemStack bottle = new ItemStack(Items.OMINOUS_BOTTLE);
            bottle.set(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER, new OminousBottleAmplifierComponent(0));
            loot.add(bottle);
            CTMEventUtils.firstVault = false;
        }

        return loot;
    }
}
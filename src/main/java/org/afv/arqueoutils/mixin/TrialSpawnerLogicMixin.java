package org.afv.arqueoutils.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.spawner.TrialSpawnerLogic;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.afv.arqueoutils.util.CTMEventUtils;
import org.afv.arqueoutils.world.CTMState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TrialSpawnerLogic.class)
public class TrialSpawnerLogicMixin {

    @ModifyExpressionValue(
        method = "ejectLootTable",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/loot/LootTable;generateLoot(Lnet/minecraft/loot/context/LootWorldContext;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;"
        )
    )
    private ObjectArrayList<ItemStack> arqueoutils$firstOminousReward(
            ObjectArrayList<ItemStack> loot,
            ServerWorld world,
            BlockPos pos,
            RegistryKey<LootTable> lootTable
    ) {
        TrialSpawnerLogic self = (TrialSpawnerLogic)(Object)this;

        if (!self.isOminous())
            return loot;

        if (world.getServer() == null) return loot;
        CTMState state = CTMState.get(world.getServer());

        if (!CTMEventUtils.firstKey || state.getPlayerMap().get(CTMState.CoreColor.GREEN) != null)
            return loot;

        int size = loot.size();
        loot.clear();
        for (int i = 0; i < size; i++) {
            loot.add(new ItemStack(Items.OMINOUS_TRIAL_KEY));
        }

        CTMEventUtils.firstKey = false;

        return loot;
    }
}
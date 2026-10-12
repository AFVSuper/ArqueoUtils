package org.afv.arqueoutils.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.LootableInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import org.afv.arqueoutils.util.CTMEventUtils;
import org.afv.arqueoutils.world.CTMState;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(LootableInventory.class)
public interface LootableInventoryMixin {
    @Shadow
    @Nullable RegistryKey<LootTable> getLootTable();

    @Shadow
    @Nullable World getWorld();

    @Inject(method = "generateLoot", at = @At(value = "HEAD"))
    default void onGenLoot(PlayerEntity player, CallbackInfo ci) {
        if (getLootTable() == null || !getLootTable().equals(LootTables.ANCIENT_CITY_CHEST)) return;

        MinecraftServer server = Objects.requireNonNull(this.getWorld()).getServer();
        if (server == null) return;

        CTMState state = CTMState.get(server);

        // ArqueoUtils.LOGGER.info("Count {}", state.getAncientCityChestsOpened());

        state.incrementAncientCityChestsOpened();
        if (state.getAncientCityChestsOpened() != 2) return;

        CTMEventUtils.nextAddEcho = true;
    }

    @Inject(method = "generateLoot", at = @At(value = "TAIL"))
    default void onGenLoot2(PlayerEntity player, CallbackInfo ci) {
        if (!CTMEventUtils.nextAddEcho) return;

        CTMEventUtils.nextAddEcho = false;

        // Already has an Echo Shard?
        Inventory chest = (Inventory) this;
        for (int i = 0; i < chest.size(); i++) {
            if (chest.getStack(i).isOf(Items.ECHO_SHARD)) {
                return;
            }
        }

        // Insert into the first empty slot
        for (int i = 0; i < chest.size(); i++) {
            if (chest.getStack(i).isEmpty()) {
                chest.setStack(i, new ItemStack(Items.ECHO_SHARD));
                chest.markDirty();
                return;
            }
        }

        // If somehow full, replace the first slot.
        chest.setStack(0, new ItemStack(Items.NETHERITE_INGOT));
        chest.markDirty();
    }
}

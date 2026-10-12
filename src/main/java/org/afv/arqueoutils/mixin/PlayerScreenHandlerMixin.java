package org.afv.arqueoutils.mixin;

import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;
import org.afv.arqueoutils.accessor.PlayerScreenHandlerAccessor;
import org.afv.arqueoutils.init.ModItems;
import org.afv.arqueoutils.inventory.ElytraSlot;
import org.afv.arqueoutils.inventory.MagnetSlot;
import org.afv.arqueoutils.track.ElytraPropertyHolder;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerScreenHandler.class)
public abstract class PlayerScreenHandlerMixin extends ScreenHandler implements ElytraPropertyHolder, PlayerScreenHandlerAccessor {
    @Mutable
    @Final
    @Shadow
    private final PlayerEntity owner;

    @Unique
    private ElytraSlot elytraSlot;

    protected PlayerScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerEntity owner) {
        super(type, syncId);
        this.owner = owner;
        this.elytraAdvancementProperty = this.addProperty(Property.create());
    }

    @Unique
    private Property elytraAdvancementProperty;

    @Unique
    private static final Identifier ELYTRA_ADVANCEMENT = Identifier.of("minecraft", "end/elytra");

    @Unique
    public void arqueoutils$forceUpdateState() {
        if (!ArqueoUtils.ENABLED_COLYTRA) return;
        if (this.owner instanceof ServerPlayerEntity serverPlayer && this.elytraAdvancementProperty != null) {
            // Safe check: If the player is still being constructed, getAdvancementTracker() will be null
            if (serverPlayer.getAdvancementTracker() == null) {
                return;
            }

            MinecraftServer server = serverPlayer.getEntityWorld().getServer();
            if (server == null) return;
            AdvancementEntry advancement = server.getAdvancementLoader().get(ELYTRA_ADVANCEMENT);
            if (advancement != null) {
                boolean isDone = serverPlayer.getAdvancementTracker().getProgress(advancement).isDone();
                // ArqueoUtils.LOGGER.info("Update isDone = {}", isDone);
                this.elytraAdvancementProperty.set(isDone ? 1 : 0);
            }
        }
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addCustomSlot(PlayerInventory playerInventory, boolean onServer, PlayerEntity player, CallbackInfo ci) {
        this.addSlot(new MagnetSlot(player, playerInventory, 41, 77, 44));
        if (ArqueoUtils.ENABLED_COLYTRA) {
            this.elytraSlot = new ElytraSlot((PlayerScreenHandler) (Object) this, player, playerInventory, 42, 77, 26);
            this.addSlot(this.elytraSlot);
        }
        this.arqueoutils$forceUpdateState();
    }

    @Inject(method = "quickMove", at = @At("HEAD"), cancellable = true)
    public void shiftClickElytra(PlayerEntity player, int ind, CallbackInfoReturnable<ItemStack> cir) {
        if (!ArqueoUtils.ENABLED_COLYTRA) return;
        PlayerScreenHandler handler = (PlayerScreenHandler) (Object) this;
        // ArqueoUtils.LOGGER.info("SLOT: {}", ind);
        Slot slot = handler.slots.get(ind);
        if (slot == null || !slot.hasStack()) return;

        ItemStack stack = slot.getStack();
        if (!stack.contains(DataComponentTypes.GLIDER)) return;
        ItemStack copy = stack.copy();

        Slot elytraSlot = handler.slots.get(47);
        if (!elytraSlot.isEnabled()) return;

        if (!(slot instanceof ElytraSlot)) {
            // ArqueoUtils.LOGGER.info("EL: {}, {}, {}", elytraSlot, elytraSlot.getStack(), elytraSlot.hasStack());
            if (!elytraSlot.hasStack()) {
                elytraSlot.setStack(stack.copy());
                slot.setStack(ItemStack.EMPTY);
                cir.setReturnValue(copy);
            }
        } else {
            // Shift-click from ElytraSlot → main inventory
            // ArqueoUtils.LOGGER.info("FROM SLOT");
            if (!player.getInventory().insertStack(stack.copy())) {
                cir.setReturnValue(ItemStack.EMPTY);
            } else {
                slot.setStack(ItemStack.EMPTY);
                cir.setReturnValue(copy);
            }
        }
    }

    @Inject(method = "quickMove", at = @At("HEAD"), cancellable = true)
    public void shiftClickMagnet(PlayerEntity player, int ind, CallbackInfoReturnable<ItemStack> cir) {
        PlayerScreenHandler handler = (PlayerScreenHandler) (Object) this;
        // ArqueoUtils.LOGGER.info("SLOT: {}", ind);
        Slot slot = handler.slots.get(ind);
        if (slot == null || !slot.hasStack()) return;

        ItemStack stack = slot.getStack();
        ItemStack copy = stack.copy();

        if (!(slot instanceof MagnetSlot)) {
            // Shift-click from main inventory → custom slot
            Slot elytraSlot = handler.slots.get(46);
            // ArqueoUtils.LOGGER.info("EL: {}, {}, {}", elytraSlot, elytraSlot.getStack(), elytraSlot.hasStack());
            if (!elytraSlot.hasStack() && stack.getItem() == ModItems.MAGNET) {
                elytraSlot.setStack(stack.copy());
                slot.setStack(ItemStack.EMPTY);
                cir.setReturnValue(copy);
            }
        } else {
            // Shift-click from ElytraSlot → main inventory
            // ArqueoUtils.LOGGER.info("FROM SLOT");
            if (!player.getInventory().insertStack(stack.copy())) {
                cir.setReturnValue(ItemStack.EMPTY);
            } else {
                slot.setStack(ItemStack.EMPTY);
                cir.setReturnValue(copy);
            }
        }
    }

    @Override
    public Property arqueoutils$getElytraProperty() {
        return this.elytraAdvancementProperty;
    }

    @Unique
    public ElytraSlot getElytraSlot() {
        return elytraSlot;
    }
}
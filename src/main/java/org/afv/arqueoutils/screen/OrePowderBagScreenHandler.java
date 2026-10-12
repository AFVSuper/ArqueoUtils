package org.afv.arqueoutils.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.afv.arqueoutils.init.ModComponents;
import org.afv.arqueoutils.init.ModItems;
import org.afv.arqueoutils.init.ModScreenHandlers;
import org.afv.arqueoutils.items.OrePowderBagData;
import org.afv.arqueoutils.items.OrePowderTypes;

public class OrePowderBagScreenHandler extends ScreenHandler {

    public static final int GRID_COLS = 3;
    public static final int GRID_ROWS = 3;
    public static final int BAG_SLOT_COUNT = GRID_COLS * GRID_ROWS;

    public static final int SLOT_START_X = 8;
    public static final int SLOT_START_Y = 8;
    /**
     * Bag columns line up with player inventory columns {@code 0, 3, 6} (two empty columns between each).
     * Each row: {@code [B] - - [B] - - [B]} over the 9-wide grid.
     */
    public static final int BAG_SLOT_SIZE = 18;
    public static final int BAG_COUNT_GAP = 4;
    public static final int BAG_NUMBER_FIELD = 42;
    public static final int BAG_INV_COLUMN_STRIDE = 3;
    public static final int PLAYER_INV_START_Y = SLOT_START_Y + GRID_ROWS * 18 + 12;

    public static int bagSlotX(int col) {
        return SLOT_START_X + col * BAG_INV_COLUMN_STRIDE * BAG_SLOT_SIZE;
    }

    public static int bagCountTextX(int col) {
        return bagSlotX(col) + BAG_SLOT_SIZE + BAG_COUNT_GAP;
    }

    private final PlayerInventory playerInventory;

    public OrePowderBagScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(ModScreenHandlers.ORE_POWDER_BAG, syncId);
        this.playerInventory = playerInventory;
        for (int r = 0; r < GRID_ROWS; r++) {
            for (int c = 0; c < GRID_ROWS; c++) {
                int idx = r * GRID_COLS + c;
                addSlot(new PowderBagSlot(idx, bagSlotX(c), SLOT_START_Y + r * 18));
            }
        }
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 9; c++) {
                addSlot(new Slot(playerInventory, c + r * 9 + 9, SLOT_START_X + c * 18, PLAYER_INV_START_Y + r * 18));
            }
        }
        for (int c = 0; c < 9; c++) {
            addSlot(new Slot(playerInventory, c, SLOT_START_X + c * 18, PLAYER_INV_START_Y + 58));
        }
    }

    public ItemStack findBagStack() {
        PlayerEntity player = playerInventory.player;
        ItemStack main = player.getMainHandStack();
        if (main.isOf(ModItems.ORE_POWDER_BAG)) {
            return main;
        }
        ItemStack off = player.getOffHandStack();
        if (off.isOf(ModItems.ORE_POWDER_BAG)) {
            return off;
        }
        for (int i = 0; i < playerInventory.size(); i++) {
            ItemStack s = playerInventory.getStack(i);
            if (s.isOf(ModItems.ORE_POWDER_BAG)) {
                return s;
            }
        }
        return ItemStack.EMPTY;
    }

    public long getStored(int typeIndex) {
        ItemStack bag = findBagStack();
        if (bag.isEmpty()) {
            return 0;
        }
        return bag.getOrDefault(ModComponents.ORE_POWDER_BAG, OrePowderBagData.EMPTY).get(typeIndex);
    }

    public void addStored(int typeIndex, long delta) {
        ItemStack bag = findBagStack();
        if (bag.isEmpty()) {
            return;
        }
        OrePowderBagData data = bag.getOrDefault(ModComponents.ORE_POWDER_BAG, OrePowderBagData.EMPTY);
        bag.set(ModComponents.ORE_POWDER_BAG, data.withDelta(typeIndex, delta));
        sendContentUpdates();
    }

    /**
     * Vanilla {@link #insertItem} merges by mutating the stack from {@link Slot#getStack()}; bag slots use a
     * virtual stack, so we insert only through {@link PowderBagSlot#insertStack}.
     */
    private boolean insertIntoPowderBagSlots(ItemStack stack, int startIndex, int endIndex) {
        boolean changed = false;
        int start = Math.max(0, startIndex);
        int end = Math.min(endIndex, BAG_SLOT_COUNT);
        for (int i = start; i < end && !stack.isEmpty(); i++) {
            Slot slot = slots.get(i);
            if (!slot.canInsert(stack) || getStored(i) <= 0) {
                continue;
            }
            int before = stack.getCount();
            slot.insertStack(stack, Integer.MAX_VALUE);
            if (stack.getCount() != before) {
                changed = true;
            }
        }
        for (int i = start; i < end && !stack.isEmpty(); i++) {
            Slot slot = slots.get(i);
            if (!slot.canInsert(stack) || getStored(i) > 0) {
                continue;
            }
            int before = stack.getCount();
            slot.insertStack(stack, Integer.MAX_VALUE);
            if (stack.getCount() != before) {
                changed = true;
            }
        }
        return changed;
    }

    @Override
    protected boolean insertItem(ItemStack stack, int startIndex, int endIndex, boolean fromContainer) {
        if (stack.isEmpty()) {
            return false;
        }
        boolean changed = false;
        if (!fromContainer && startIndex < BAG_SLOT_COUNT) {
            int bagStart = Math.max(startIndex, 0);
            int bagEnd = Math.min(endIndex, BAG_SLOT_COUNT);
            if (bagStart < bagEnd) {
                changed |= insertIntoPowderBagSlots(stack, bagStart, bagEnd);
            }
            if (endIndex <= BAG_SLOT_COUNT) {
                return changed;
            }
            startIndex = Math.max(BAG_SLOT_COUNT, startIndex);
        }
        return changed || super.insertItem(stack, startIndex, endIndex, fromContainer);
    }

    /**
     * Vanilla shift-moves in a loop while the source slot still {@linkplain Slot#hasStack() has a stack}. Bag
     * slots stay "non-empty" while storage {@code > 64}, so only one quick move per click is applied here.
     */
    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (actionType == SlotActionType.QUICK_MOVE && slotIndex >= 0 && slotIndex < BAG_SLOT_COUNT) {
            this.quickMove(player, slotIndex);
            return;
        }
        super.onSlotClick(slotIndex, button, actionType, player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        Slot slot = this.slots.get(slotIndex);
        if (slot == null || !slot.hasStack()) {
            return ItemStack.EMPTY;
        }

        ItemStack quickMoveReturn = slot.getStack().copy();

        // ArqueoUtils.LOGGER.info("SlotIndex: {}", slotIndex);
        if (slotIndex < 9) {
            int toMove = (int) Math.min(64, getStored(slotIndex));
            if (toMove <= 0) {
                return ItemStack.EMPTY;
            }
            ItemStack extracted = slot.takeStack(toMove);
            if (extracted.isEmpty()) {
                return ItemStack.EMPTY;
            }
            int before = extracted.getCount();
            insertItem(extracted, BAG_SLOT_COUNT, this.slots.size(), true);
            if (extracted.getCount() == before) {
                addStored(slotIndex, before);
                return ItemStack.EMPTY;
            }
            if (!extracted.isEmpty()) {
                addStored(slotIndex, extracted.getCount());
            }
            slot.markDirty();
            return quickMoveReturn;
        }

        ItemStack playerStack = slot.getStack();
        ItemStack mergeStack = playerStack.copy();
        int amountIn = mergeStack.getCount();
        insertItem(mergeStack, 0, BAG_SLOT_COUNT, false);
        int moved = amountIn - mergeStack.getCount();
        if (moved <= 0) {
            return ItemStack.EMPTY;
        }
        playerStack.decrement(moved);
        if (playerStack.isEmpty()) {
            slot.setStack(ItemStack.EMPTY);
        }
        slot.markDirty();
        return quickMoveReturn;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return !findBagStack().isEmpty();
    }

    private class PowderBagSlot extends Slot {
        private final int typeIndex;

        PowderBagSlot(int typeIndex, int x, int y) {
            super(new SimpleInventory(1), 0, x, y);
            this.typeIndex = typeIndex;
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            if (!stack.isOf(ModItems.ORE_POWDER)) {
                return false;
            }
            String t = stack.get(ModComponents.ORE_POWDER_TYPE);
            return OrePowderTypes.ORDER.get(typeIndex).equals(t);
        }

        @Override
        public ItemStack getStack() {
            long stored = OrePowderBagScreenHandler.this.getStored(typeIndex);
            if (stored <= 0) {
                return ItemStack.EMPTY;
            }
            ItemStack stack = new ItemStack(ModItems.ORE_POWDER);
            stack.set(ModComponents.ORE_POWDER_TYPE, OrePowderTypes.ORDER.get(typeIndex));
            stack.setCount((int) Math.min(64, stored));
            return stack;
        }

        @Override
        public void setStack(ItemStack stack) {
            if (stack.isEmpty() || !canInsert(stack)) {
                return;
            }
            OrePowderBagScreenHandler.this.addStored(typeIndex, stack.getCount());
        }

        @Override
        public ItemStack insertStack(ItemStack stack, int maxCount) {
            if (!canInsert(stack)) {
                return stack;
            }
            int insert = Math.min(stack.getCount(), maxCount);
            OrePowderBagScreenHandler.this.addStored(typeIndex, insert);
            stack.decrement(insert);
            markDirty();
            return stack;
        }

        @Override
        public ItemStack takeStack(int amount) {
            long stored = OrePowderBagScreenHandler.this.getStored(typeIndex);
            if (stored <= 0) {
                return ItemStack.EMPTY;
            }
            int take = (int) Math.min(amount, Math.min(64, stored));
            OrePowderBagScreenHandler.this.addStored(typeIndex, -take);
            ItemStack stack = new ItemStack(ModItems.ORE_POWDER, take);
            stack.set(ModComponents.ORE_POWDER_TYPE, OrePowderTypes.ORDER.get(typeIndex));
            markDirty();
            return stack;
        }

        @Override
        public int getMaxItemCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public int getMaxItemCount(ItemStack stack) {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean canTakeItems(PlayerEntity playerEntity) {
            return OrePowderBagScreenHandler.this.getStored(typeIndex) > 0;
        }

        @Override
        public void markDirty() {
            OrePowderBagScreenHandler.this.sendContentUpdates();
        }
    }
}

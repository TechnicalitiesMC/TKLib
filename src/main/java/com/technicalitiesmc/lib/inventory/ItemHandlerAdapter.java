package com.technicalitiesmc.lib.inventory;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

public class ItemHandlerAdapter implements IItemHandler {

    private final ItemHolder inventory;

    public ItemHandlerAdapter(ItemHolder inventory) {
        this.inventory = inventory;
    }

    @Override
    public int getSlots() {
        return inventory.getSize();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return inventory.get(slot);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        var currentStack = inventory.get(slot);

        if (!currentStack.isEmpty() && !ItemHandlerHelper.canItemStacksStack(currentStack, stack)) {
            return stack;
        }

        if (currentStack.isEmpty()) {
            if (!simulate) {
                inventory.set(slot, stack);
            }
            return ItemStack.EMPTY;
        } else {
            var maxSize = currentStack.getMaxStackSize();
            var currentSize = currentStack.getCount();
            var inserted = Math.min(maxSize - currentSize, stack.getCount());
            if (!simulate) {
                currentStack.grow(inserted);
            }
            ItemStack leftover = stack.copy();
            leftover.shrink(inserted);
            return leftover;
        }
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        var extracted = inventory.get(slot);

        if (extracted.isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (simulate) extracted = extracted.copy();
        return extracted.split(amount);
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return true;
    }

}

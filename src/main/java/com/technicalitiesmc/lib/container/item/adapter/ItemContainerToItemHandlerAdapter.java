package com.technicalitiesmc.lib.container.item.adapter;

import com.technicalitiesmc.lib.container.item.ItemContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * A basic {@link IItemHandlerModifiable} implementation derived from an {@link ItemContainer}.
 */
public class ItemContainerToItemHandlerAdapter implements IItemHandlerModifiable {

    private final ItemContainer inventory;

    public ItemContainerToItemHandlerAdapter(ItemContainer inventory) {
        this.inventory = inventory;
    }

    @Override
    public int getSlots() {
        return inventory.size();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        Objects.checkIndex(slot, getSlots());
        return inventory.get(slot);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        Objects.checkIndex(slot, getSlots());
        var currentStack = inventory.get(slot);

        if (!currentStack.isEmpty() && !ItemHandlerHelper.canItemStacksStack(currentStack, stack)) {
            return stack;
        }

        if (currentStack.isEmpty()) {
            if (!inventory.isValid(slot, stack)) {
                return stack;
            }
            if (!simulate) {
                inventory.set(slot, stack);
            }
            return ItemStack.EMPTY;
        } else {
            var maxSize = currentStack.getMaxStackSize();
            var currentSize = currentStack.getCount();
            var inserted = Math.min(maxSize - currentSize, stack.getCount());
            var newStack = currentStack.copy();
            newStack.grow(inserted);
            if (!inventory.isValid(slot, newStack)) {
                return stack;
            }
            if (!simulate) {
                inventory.set(slot, newStack);
            }
            var leftover = stack.copy();
            leftover.shrink(inserted);
            return leftover;
        }
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        Objects.checkIndex(slot, getSlots());
        var currentStack = inventory.get(slot);

        if (currentStack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (simulate) {
            return currentStack.copy().split(amount);
        } else {
            var leftover = currentStack.copy();
            var extracted = leftover.split(amount);
            inventory.set(slot, leftover);
            return extracted;
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        Objects.checkIndex(slot, getSlots());
        return inventory.getMaxStackSize(slot);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        Objects.checkIndex(slot, getSlots());
        return inventory.isValid(slot, stack);
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        Objects.checkIndex(slot, getSlots());
        inventory.set(slot, stack);
    }

}

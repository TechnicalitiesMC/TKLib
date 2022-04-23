package com.technicalitiesmc.lib.container.item.adapter;

import com.technicalitiesmc.lib.container.item.ItemContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

public class ItemHandlerToItemContainerAdapter implements ItemContainer {

    private final IItemHandler itemHandler;

    public ItemHandlerToItemContainerAdapter(IItemHandler itemHandler) {
        this.itemHandler = itemHandler;
    }

    @Override
    public int size() {
        return itemHandler.getSlots();
    }

    @NotNull
    @Override
    public ItemStack get(int slot) {
        return itemHandler.getStackInSlot(slot);
    }

    @Override
    public void set(int slot, @NotNull ItemStack stack) {
        if (itemHandler instanceof IItemHandlerModifiable modifiable) {
            modifiable.setStackInSlot(slot, stack);
        } else {
            itemHandler.extractItem(slot, Integer.MAX_VALUE, false);
            itemHandler.insertItem(slot, stack, false);
        }
    }

    @Override
    public boolean isValid(int slot, @NotNull ItemStack stack) {
        return itemHandler.isItemValid(slot, stack);
    }

    @Override
    public int getMaxStackSize(int slot) {
        return itemHandler.getSlotLimit(slot);
    }

    @NotNull
    @Override
    public IItemHandlerModifiable asItemHandler() {
        if (itemHandler instanceof IItemHandlerModifiable modifiable) {
            return modifiable;
        }
        return ItemContainer.super.asItemHandler();
    }

}

package com.technicalitiesmc.lib.container.item;

import net.minecraft.world.item.ItemStack;

import java.util.Objects;

/**
 * Basic implementation of an {@link ItemContainer} slice.
 */
public class ItemContainerSlice implements ItemContainer {

    private final ItemContainer parent;
    private final int from, size;

    public ItemContainerSlice(ItemContainer parent, int from, int size) {
        this.parent = parent;
        this.from = from;
        this.size = size;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public ItemStack get(int slot) {
        Objects.checkIndex(slot, size);
        return parent.get(slot + from);
    }

    @Override
    public void set(int slot, ItemStack stack) {
        Objects.checkIndex(slot, size);
        parent.set(slot + from, stack);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        Objects.checkIndex(slot, size);
        return parent.isValid(slot + from, stack);
    }

    @Override
    public ItemContainer slice(int from, int to) {
        Objects.checkFromToIndex(from, to, size());
        return new ItemContainerSlice(parent, this.from + from, to - from);
    }

}

package com.technicalitiesmc.lib.inventory;

import net.minecraft.world.item.ItemStack;

public class ItemHolderSlice implements ItemHolder {

    private final ItemHolder parent;
    private final int from, size;

    public ItemHolderSlice(ItemHolder parent, int from, int size) {
        this.parent = parent;
        this.from = from;
        this.size = size;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public ItemStack get(int slot) {
        return parent.get(slot + from);
    }

    @Override
    public void set(int slot, ItemStack stack) {
        parent.set(slot + from, stack);
    }

}

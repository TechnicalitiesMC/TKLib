package com.technicalitiesmc.lib.container.item.adapter;

import com.technicalitiesmc.lib.container.item.ItemContainer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ItemContainerToContainerAdapter implements Container {

    protected final ItemContainer itemContainer;

    public ItemContainerToContainerAdapter(ItemContainer itemContainer) {
        this.itemContainer = itemContainer;
    }

    @Override
    public int getContainerSize() {
        return itemContainer.size();
    }

    @Override
    public boolean isEmpty() {
        return itemContainer.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return itemContainer.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amt) {
        ItemStack stack = itemContainer.get(slot);
        ItemStack split = stack.split(amt);
        itemContainer.set(slot, stack);
        return split;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = itemContainer.get(slot);
        itemContainer.set(slot, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        itemContainer.set(slot, stack);
    }

    @Override
    public void setChanged() {
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        itemContainer.clear();
    }

}

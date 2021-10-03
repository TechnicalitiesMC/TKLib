package com.technicalitiesmc.lib.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class VanillaContainerAdapter implements Container {

    private final ItemHolder inventory;

    public VanillaContainerAdapter(ItemHolder inventory) {
        this.inventory = inventory;
    }

    @Override
    public int getContainerSize() {
        return inventory.getSize();
    }

    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return inventory.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amt) {
        ItemStack stack = inventory.get(slot);
        ItemStack split = stack.split(amt);
        inventory.set(slot, stack);
        return split;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = inventory.get(slot);
        inventory.set(slot, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        inventory.set(slot, stack);
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
        inventory.clear();
    }

}

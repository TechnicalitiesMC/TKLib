package com.technicalitiesmc.lib.container.item.adapter;

import com.technicalitiesmc.lib.container.item.ItemContainer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ContainerToItemContainerAdapter implements ItemContainer {

    private final Container container;

    public ContainerToItemContainerAdapter(Container container) {
        this.container = container;
    }

    @Override
    public int size() {
        return container.getContainerSize();
    }

    @NotNull
    @Override
    public ItemStack get(int slot) {
        return container.getItem(slot);
    }

    @Override
    public void set(int slot, @NotNull ItemStack stack) {
        container.setItem(slot, stack);
    }

    @Override
    public boolean isValid(int slot, @NotNull ItemStack stack) {
        return container.canPlaceItem(slot, stack);
    }

    @Override
    public int getMaxStackSize(int slot) {
        return container.getMaxStackSize();
    }

    @Override
    public void clear() {
        container.clearContent();
    }

    @Override
    public boolean isEmpty() {
        return container.isEmpty();
    }

    @NotNull
    @Override
    public Container asVanillaContainer() {
        return container;
    }

}

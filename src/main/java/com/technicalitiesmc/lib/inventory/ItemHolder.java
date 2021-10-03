package com.technicalitiesmc.lib.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public interface ItemHolder {

    int getSize();

    ItemStack get(int slot);

    void set(int slot, ItemStack stack);

    default void clear() {
        for (int i = 0; i < getSize(); i++) {
            set(i, ItemStack.EMPTY);
        }
    }

    default boolean isEmpty() {
        for (int i = 0; i < getSize(); i++) {
            if (!get(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    default ItemHolder slice(int fromIncluding, int toExcluding) {
        return new ItemHolderSlice(this, fromIncluding, toExcluding - fromIncluding);
    }

    default IItemHandler asItemHandler() {
        return new ItemHandlerAdapter(this);
    }

    default Container asVanillaContainer() {
        return new VanillaContainerAdapter(this);
    }

}

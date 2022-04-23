package com.technicalitiesmc.lib.recipe;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public interface CustomOutputRecipe<C extends Container> extends CustomRecipe<C> {

    @Override
    default ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

}

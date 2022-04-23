package com.technicalitiesmc.lib.recipe;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

public interface CustomRecipe<C extends Container> extends Recipe<C> {

    @Override
    default ItemStack assemble(C container) {
        return getResultItem().copy();
    }

    @Override
    default boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    default boolean isSpecial() {
        return true;
    }

}

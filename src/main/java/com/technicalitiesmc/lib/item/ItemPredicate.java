package com.technicalitiesmc.lib.item;

import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

@FunctionalInterface
public interface ItemPredicate extends Predicate<ItemStack> {
}

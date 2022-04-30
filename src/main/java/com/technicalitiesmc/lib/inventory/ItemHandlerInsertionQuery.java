package com.technicalitiesmc.lib.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.PrimitiveIterator;
import java.util.stream.IntStream;

public class ItemHandlerInsertionQuery {

    public static PrimitiveIterator.OfInt defaultVisitOrder(int size) {
        return IntStream.range(0, size).iterator();
    }

    private final IItemHandler inventory;
    private final int size;

    private final List<ItemStack> items;
    private final NonNullList<ItemStack> inserted;

    private Insertion lastInsertion;
    private boolean committed;

    public ItemHandlerInsertionQuery(IItemHandler inventory) {
        this.inventory = inventory;
        this.size = inventory.getSlots();

        this.items = new ArrayList<>();
        for (var i = 0; i < size; i++) {
            items.add(null);
        }
        this.inserted = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    public Insertion insert(ItemStack stack) {
        return insert(stack, defaultVisitOrder(size));
    }

    public Insertion insert(ItemStack stack, PrimitiveIterator.OfInt visitOrder) {
        stack = stack.copy();
        var maxStackSize = stack.getMaxStackSize();
        var insertedStacks = NonNullList.withSize(size, ItemStack.EMPTY);
        while (visitOrder.hasNext()) {
            var i = visitOrder.nextInt();
            if (!couldInsert(i, stack)) {
                continue;
            }

            var alreadyInserted = inserted.get(i);
            var maxInserted = Math.min(maxStackSize - alreadyInserted.getCount(), stack.getCount());

            var totalInserted = stack.copy();
            totalInserted.setCount(alreadyInserted.getCount() + maxInserted);
            var totalLeftover = inventory.insertItem(i, totalInserted, true);
            if (totalLeftover.getCount() > maxInserted) {
                continue;
            }

            var totalInsertedAmt = totalInserted.getCount() - totalLeftover.getCount();
            var insertedAmt = totalInsertedAmt - alreadyInserted.getCount();
            insertedStacks.set(i, stack.split(insertedAmt));
        }
        return lastInsertion = new Insertion(stack, insertedStacks);
    }

    public void commit() {
        if (committed) {
            throw new IllegalStateException("This query has already been committed.");
        }
        committed = true;

        for (var i = 0; i < size; i++) {
            var stack = inserted.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            inventory.insertItem(i, stack, false);
        }
    }

    private boolean couldInsert(int slot, ItemStack stack) {
        var currentItem = items.get(slot);
        if (currentItem == null) {
            items.set(slot, currentItem = inventory.getStackInSlot(slot).copy());
        }
        if (currentItem.isEmpty()) {
            return true;
        }
        if (currentItem.getCount() >= currentItem.getMaxStackSize()) {
            return false;
        }
        return ItemHandlerHelper.canItemStacksStack(currentItem, stack);
    }

    public class Insertion {

        private final ItemStack leftover;
        private final NonNullList<ItemStack> insertedStacks;

        private Insertion(ItemStack leftover, NonNullList<ItemStack> insertedStacks) {
            this.leftover = leftover;
            this.insertedStacks = insertedStacks;
        }

        public ItemStack getLeftover() {
            return leftover;
        }

        public void commit() {
            if (lastInsertion != this) {
                throw new IllegalStateException("Only the most recent insertion can be committed.");
            }
            lastInsertion = null;

            for (var i = 0; i < size; i++) {
                var stack = insertedStacks.get(i);
                if (stack.isEmpty()) {
                    continue;
                }

                var currentItem = items.get(i);
                if (currentItem.isEmpty()) {
                    items.set(i, stack);
                } else {
                    currentItem.grow(stack.getCount());
                }

                var currentlyInserted = inserted.get(i);
                if (currentlyInserted.isEmpty()) {
                    inserted.set(i, stack);
                } else {
                    currentlyInserted.grow(stack.getCount());
                }
            }
        }

    }

}

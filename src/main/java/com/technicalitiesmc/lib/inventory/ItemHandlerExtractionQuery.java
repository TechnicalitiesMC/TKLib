package com.technicalitiesmc.lib.inventory;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.PrimitiveIterator;
import java.util.stream.IntStream;

public class ItemHandlerExtractionQuery {

    public static PrimitiveIterator.OfInt defaultVisitOrder(int size) {
        return IntStream.range(0, size).iterator();
    }

    private final IItemHandler inventory;
    private final int size;

    private final List<ItemStack> items;
    private final int[] extracted;

    private Extraction lastExtraction;
    private boolean committed;

    public ItemHandlerExtractionQuery(IItemHandler inventory) {
        this.inventory = inventory;
        this.size = inventory.getSlots();

        this.items = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            items.add(null);
        }
        this.extracted = new int[size];
    }

    public Extraction extract(ItemFilter filter) {
        return extract(filter, defaultVisitOrder(size));
    }

    public Extraction extract(ItemFilter filter, PrimitiveIterator.OfInt visitOrder) {
        var stack = ItemStack.EMPTY;
        var extractedAmount = 0;
        var minExtracted = 0;
        var maxExtracted = 0;
        var extractedAmounts = new int[size];
        while (visitOrder.hasNext()) {
            var i = visitOrder.nextInt();
            if (stack.isEmpty()) {
                var matchedFilter = getMatch(i, filter);
                if (matchedFilter == null) {
                    continue;
                }
                stack = items.get(i).copy();
                var mode = matchedFilter.getMode();
                var amount = matchedFilter.getAmount();
                minExtracted = mode == ItemFilter.AmountMatchMode.AT_MOST ? 0 : amount;
                maxExtracted = mode == ItemFilter.AmountMatchMode.AT_LEAST ? 64 : amount;
            } else if (!ItemHandlerHelper.canItemStacksStack(stack, getStack(i))) {
                continue;
            }
            var s = getStack(i);
            var extracted = Math.min(s.getCount(), maxExtracted - extractedAmount);
            extractedAmounts[i] = extracted;
            extractedAmount += extracted;
            if (extractedAmount == maxExtracted) {
                break;
            }
        }
        if (extractedAmount < minExtracted) {
            return new Extraction(ItemStack.EMPTY, null, 0);
        }
        stack.setCount(extractedAmount);
        return lastExtraction = new Extraction(stack, extractedAmounts, minExtracted);
    }

    public void commit() {
        if (committed) {
            throw new IllegalStateException("This query has already been committed.");
        }
        committed = true;

        for (var i = 0; i < size; i++) {
            var amt = extracted[i];
            if (amt != 0) {
                inventory.extractItem(i, amt, false);
            }
        }
    }

    private ItemStack getStack(int slot) {
        ItemStack currentItem = items.get(slot);
        if (currentItem == null) {
            items.set(slot, currentItem = inventory.extractItem(slot, 64, true).copy());
        }
        return currentItem;
    }

    private ItemFilter.Simple getMatch(int slot, ItemFilter filter) {
        var currentItem = getStack(slot);
        if (currentItem.isEmpty()) {
            return null;
        }
        if (filter instanceof ItemFilter.Combined c) {
            return c.getMatchingFilter(currentItem);
        }
        if (filter instanceof ItemFilter.Simple s) {
            return filter.test(currentItem) ? s : null;
        }
        throw new IllegalArgumentException("Custom filter implementations are not allowed in extraction queries.");
    }

    public class Extraction {

        private final ItemStack extracted;
        private final int[] extractedAmounts;
        private final int minExtracted;

        private Extraction(ItemStack extracted, int[] extractedAmounts, int minExtracted) {
            this.extracted = extracted;
            this.extractedAmounts = extractedAmounts;
            this.minExtracted = minExtracted;
        }

        public ItemStack getExtracted() {
            return extracted;
        }

        public void commit() {
            if (lastExtraction != this) {
                throw new IllegalStateException("Only the most recent extraction can be committed.");
            }
            lastExtraction = null;

            if (extracted.isEmpty()) {
                throw new IllegalStateException("Attempted to commit a failed extraction.");
            }

            for (int i = 0; i < size; i++) {
                int amt = extractedAmounts[i];
                if (amt == 0) {
                    continue;
                }

                var currentItem = items.get(i);
                currentItem.shrink(amt);
                ItemHandlerExtractionQuery.this.extracted[i] += amt;
            }
        }

        public boolean commitAtMost(int maxExtracted) {
            if (lastExtraction != this) {
                throw new IllegalStateException("Only the most recent extraction can be committed.");
            }
            lastExtraction = null;

            if (extracted.isEmpty()) {
                throw new IllegalStateException("Attempted to commit a failed extraction.");
            }

            if (maxExtracted < minExtracted) {
                return false;
            }

            var left = maxExtracted;
            for (var i = 0; i < size && left > 0; i++) {
                var amt = extractedAmounts[i];
                if (amt == 0) {
                    continue;
                }

                var actualAmt = Math.min(left, amt);

                var currentItem = items.get(i);
                currentItem.shrink(actualAmt);
                ItemHandlerExtractionQuery.this.extracted[i] += actualAmt;
                left -= actualAmt;
            }
            return true;
        }

    }

}
